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
import com.fedosique.carsharing.storage.{CarStorage, UserStorage}
import io.circe.syntax.EncoderOps

import java.util.UUID


class ClientServiceGenericImpl[F[_]: Monad, DbEffect[_]: Monad](carStorage: CarStorage[DbEffect],
                                                                  userStorage: UserStorage[DbEffect])
                                                                 (implicit evalDb: DbEffect ~> F, actorSystem: ActorSystem) extends ClientService[F] {

  override def getCar(id: UUID): F[Option[Car]] = evalDb(carStorage.get(id).map(_.filterNot(_.status.isOccupied)))

  override def availableCars(loc: Location): F[Seq[Car]] =
    evalDb(carStorage.listAll()
      .map(cars =>
        cars.filterNot(_.status.isOccupied)
          .sortBy(car => DistanceCalculator.calculateDistanceInKM(loc, car.location))
      ))

  override def occupyCar(carId: UUID, userId: UUID): F[Car] = getCar(carId).flatMap {
    case Some(car) => evalDb(userStorage.get(userId).flatMap {
      case Some(user) if !user.isRenting =>
        Http().singleRequest(occupyRequest(carId, userId)) // TODO: add retries or recover

        userStorage.update(userId, user.copy(isRenting = true)).flatMap(_ =>
          carStorage.update(carId, car.copy(status = car.status.copy(isOccupied = true, occupiedBy = Some(userId)))))

      case Some(_) => throw UserAlreadyRentingException(userId)
      case _ => throw UserNotFoundException(userId)
    })
    case _ => throw CarNotFoundException(carId)
  }

  override def leaveCar(carId: UUID, userId: UUID): F[Car] = evalDb(carStorage.get(carId).flatMap {
    case Some(car) if car.status.occupiedBy.isDefined => userStorage.get(userId).flatMap {
      case Some(user) if car.status.occupiedBy.contains(userId) =>
        Http().singleRequest(leaveRequest(carId, userId))

        userStorage.update(userId, user.copy(isRenting = false)).flatMap(_ =>
          carStorage.update(carId, car.copy(status = car.status.copy(isOccupied = false, occupiedBy = None))))

      case Some(_) => throw CarOccupiedByOtherUser(carId)
      case _ => throw UserNotFoundException(userId)
    }
    case Some(_) => throw CarNotOccupiedException(carId)
    case _ => throw CarNotFoundException(carId)
  })


  private def occupyRequest(carId: UUID, userId: UUID) = HttpRequest(
    method = HttpMethods.POST,
    uri = "http://localhost:8080/events",
    entity = HttpEntity(
      ContentTypes.`application/json`,
      s"${ServerSentEvent(s"$carId $userId", "occupy").asJson.noSpaces}"
    )
  )

  private def leaveRequest(carId: UUID, userId: UUID) = HttpRequest(
    method = HttpMethods.POST,
    uri = "http://localhost:8080/events",
    entity = HttpEntity(
      ContentTypes.`application/json`,
      s"${ServerSentEvent(s"$carId $userId", "leave").asJson.noSpaces}"
    )
  )

}
