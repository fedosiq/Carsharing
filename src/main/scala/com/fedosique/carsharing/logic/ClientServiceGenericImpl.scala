package com.fedosique.carsharing.logic

import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.{Monad, ~>}
import com.fedosique.carsharing._
import com.fedosique.carsharing.storage.{CarStorage, UserStorage}

import java.util.UUID
import scala.concurrent.ExecutionContext


class ClientServiceGenericImpl[F[_]: Monad, DbEffect[_]: Monad](carStorage: CarStorage[DbEffect],
                                                                userStorage: UserStorage[DbEffect])
                                                               (implicit evalDb: DbEffect ~> F, ec: ExecutionContext) extends ClientService[F] {

  override def getCar(id: UUID): F[Option[Car]] = evalDb(carStorage.get(id).map(_.filterNot(_.status.isOccupied)))

  override def availableCars(loc: Location): F[Seq[Car]] =
    evalDb(carStorage.listAll()
      .map(cars =>
        cars.filterNot(_.status.isOccupied)
          .sortBy(car => DistanceCalculator.calculateDistanceInKM(loc, car.location))
      ))

  override def occupyCar(carId: UUID, userId: UUID): F[Car] = getCar(carId).flatMap {
    case Some(car) => evalDb(userStorage.get(userId).flatMap {
      case Some(user) if !user.isRenting => userStorage.update(userId, user.copy(isRenting = true)).flatMap(_ =>
        carStorage.update(carId, car.copy(status = car.status.copy(isOccupied = true, occupiedBy = Some(userId)))))
      case Some(_) => throw UserAlreadyRentingException(userId)
      case _ => throw UserNotFoundException(userId)
    })
    case _ => throw CarNotFoundException(carId)
  }

  override def leaveCar(carId: UUID, userId: UUID): F[Car] = evalDb(carStorage.get(carId).flatMap {
    case Some(car) if car.status.occupiedBy.isDefined => userStorage.get(userId).flatMap {
      case Some(user) if car.status.occupiedBy.contains(userId) =>
        userStorage.update(userId, user.copy(isRenting = false)).flatMap(_ =>
          carStorage.update(carId, car.copy(status = car.status.copy(isOccupied = false, occupiedBy = None))))
      case Some(_) => throw CarOccupiedByOtherUser(carId)
      case _ => throw UserNotFoundException(userId)
    }
    case Some(_) => throw CarNotOccupiedException(carId)
    case _ => throw CarNotFoundException(carId)
  })
}
