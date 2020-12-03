package com.fedosique.carsharing.logic

import java.util.UUID

import com.fedosique.carsharing.storage.CarStorage
import com.fedosique.carsharing.{Car, DistanceCalculator, Location}
import monix.eval.Task


class ClientServiceImpl(storage: CarStorage[Task]) extends ClientService[Task] {

  override def getCar(id: UUID): Task[Option[Car]] = storage.get(id)

  override def freeCars(loc: Location): Task[Seq[Car]] =
    storage.listAll()
      .map(cars =>
        cars.filterNot(_.status.isOccupied)
          .sortBy(car => DistanceCalculator.calculateDistanceInKM(loc, car.location))
      )
}
