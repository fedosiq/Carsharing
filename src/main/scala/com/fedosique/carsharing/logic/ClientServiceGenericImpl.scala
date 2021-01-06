package com.fedosique.carsharing.logic

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.{Monad, ~>}
import com.fedosique.carsharing._
import com.fedosique.carsharing.api.ServerSentEventCodec.sseEncoder
import com.fedosique.carsharing.logic.ClientServiceGenericImpl.{calcDebt, leaveRequest, occupyRequest}
import com.fedosique.carsharing.model.{Car, Event, Location}
import com.fedosique.carsharing.storage.{CarStorage, EventStorage, UserStorage}
import io.circe.syntax.EncoderOps

import java.time.Instant
import java.util.UUID
import java.util.concurrent.TimeUnit


class ClientServiceGenericImpl[F[_] : Monad, DbEffect[_] : Monad](carStorage: CarStorage[DbEffect],
                                                                  userStorage: UserStorage[DbEffect],
                                                                  eventStorage: EventStorage[DbEffect])
                                                                 (implicit evalDb: DbEffect ~> F,
                                                                  actorSystem: ActorSystem) extends ClientService[F] {

  override def getCar(id: UUID): F[Option[Car]] = evalDb(carStorage.get(id).map(_.filterNot(_.isOccupied)))

  override def availableCars(loc: Location, limit: Int = 20): F[Seq[Car]] =
    evalDb(carStorage.listAll().map(cars =>
      cars
        .filterNot(_.isOccupied)
        .sortBy(car => DistanceCalculator.calculateDistanceInKM(loc, car.location))
        .take(limit)
    ))

  override def occupyCar(carId: UUID, userId: UUID): F[Car] = getCar(carId).flatMap {
    case Some(car) => evalDb(userStorage.get(userId).flatMap {
      case Some(user) if !user.isRenting =>

        Http().singleRequest(occupyRequest(carId, userId)) // TODO: add retries or recover
        for {
          car <- carStorage.update(carId, car.copy(status = car.status.copy(occupiedBy = Some(userId))))
          _ <- userStorage.update(userId, user.copy(isRenting = true))
          _ <- eventStorage.put(Event(UUID.randomUUID(), "occupy", carId, userId, car.location, Instant.now()))
        } yield car

      case Some(_) => throw UserAlreadyRentingException(userId)
      case _ => throw UserNotFoundException(userId)
    })
    case _ => throw CarNotFoundException(carId)
  }

  override def leaveCar(carId: UUID, userId: UUID): F[Car] = evalDb(carStorage.get(carId).flatMap {
    case Some(car) if car.status.occupiedBy.isDefined => userStorage.get(userId).flatMap {
      case Some(user) if car.status.occupiedBy.contains(userId) =>

        Http().singleRequest(leaveRequest(carId, userId))
        val leaveTime = Instant.now()
        eventStorage.getLastOccupationTime(userId).flatMap {
          case Some(occupationTime) => for {
            car <- carStorage.update(carId, car.copy(status = car.status.copy(occupiedBy = None)))
            _ <- userStorage.update(userId, user.copy(isRenting = false, debt = user.debt + calcDebt(occupationTime, leaveTime, car.price)))
            _ <- eventStorage.put(Event(UUID.randomUUID(), "leave", carId, userId, car.location, leaveTime))
          } yield car
          case _ => throw CarNotOccupiedException(carId)
        }

      case Some(_) => throw CarOccupiedByOtherUser(carId)
      case _ => throw UserNotFoundException(userId)
    }
    case Some(_) => throw CarNotOccupiedException(carId)
    case _ => throw CarNotFoundException(carId)
  })
}


object ClientServiceGenericImpl {

  private def occupyRequest(carId: UUID, userId: UUID) = HttpRequest(
    method = HttpMethods.POST,
    uri = "http://localhost:8080/api/v1/events",
    entity = HttpEntity(
      ContentTypes.`application/json`,
      s"${ServerSentEvent(s"$carId $userId", "occupy").asJson.noSpaces}"
    )
  )

  private def leaveRequest(carId: UUID, userId: UUID) = HttpRequest(
    method = HttpMethods.POST,
    uri = "http://localhost:8080/api/v1/events",
    entity = HttpEntity(
      ContentTypes.`application/json`,
      s"${ServerSentEvent(s"$carId $userId", "leave").asJson.noSpaces}"
    )
  )

  private def calcDebt(occupyTime: Instant, leaveTime: Instant, price: Double): Double =
    TimeUnit.MILLISECONDS.toMinutes(leaveTime.toEpochMilli - occupyTime.toEpochMilli) * price
}
