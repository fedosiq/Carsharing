package com.fedosique.carsharing.logic

import com.fedosique.carsharing.storage.{CarStorage, UserStorage}
import com.fedosique.carsharing._
import monix.eval.Task

import java.util.UUID


class ClientServiceImpl(carStorage: CarStorage[Task], userStorage: UserStorage[Task]) extends ClientService[Task] {

  override def getCar(id: UUID): Task[Option[Car]] = carStorage.get(id).map(_.filterNot(_.status.isOccupied))

  override def availableCars(loc: Location): Task[Seq[Car]] =
    carStorage.listAll()
      .map(cars =>
        cars.filterNot(_.status.isOccupied)
          .sortBy(car => DistanceCalculator.calculateDistanceInKM(loc, car.location))
      )

  override def occupyCar(carId: UUID, userId: UUID): Task[Car] = getCar(carId).flatMap {
    case Some(car) => userStorage.get(userId).flatMap {
      case Some(user) if !user.isRenting => userStorage.update(userId, user.copy(isRenting = true)).flatMap(_ =>
        carStorage.update(carId, car.copy(status = car.status.copy(isOccupied = true, occupiedBy = Some(userId)))))
      case Some(_) => throw UserAlreadyRentingException(userId)
      case _ => throw UserNotFoundException(userId)
    }
    case _ => throw CarNotFoundException(carId)
  }

  override def leaveCar(carId: UUID, userId: UUID): Task[Car] = carStorage.get(carId).flatMap {
    case Some(car) if car.status.isOccupied => userStorage.get(userId).flatMap {
      case Some(user) if car.status.occupiedBy.contains(userId) =>
        userStorage.update(userId, user.copy(isRenting = false)).flatMap(_ =>
          carStorage.update(carId, car.copy(status = car.status.copy(isOccupied = false, occupiedBy = None))))
//    case Some(_) => throw CarIsRentedByOtherUser(carId) // возможно избыточно
      case _ => throw UserNotFoundException(userId)
    }
//  case Some(_) => throw CarNotOccupiedException(carId)
    case _ => throw CarNotFoundException(carId)
  }
}
