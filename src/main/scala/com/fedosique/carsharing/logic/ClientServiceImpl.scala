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

  //TODO: запретить занимать машину, если юзер уже занял какую-нибудь машину
  override def occupyCar(carId: UUID, userId: UUID): Task[Car] = getCar(carId).flatMap {
    case Some(car) if !car.status.isOccupied => userStorage.get(userId).flatMap {
      case Some(user) => carStorage.update(carId, car.copy(status = car.status.copy(isOccupied = true, occupiedBy = Some(user))))
      case _ => throw UserNotFoundException(userId)
    }
    case _ => throw CarAlreadyOccupiedException(carId)
  }
}