package com.fedosique.carsharing.logic

import com.fedosique.carsharing.storage.CarStorage
import com.fedosique.carsharing.{Car, DistanceCalculator, Location}
import monix.eval.Task

import java.util.UUID


class ClientServiceImpl(storage: CarStorage[Task]) extends ClientService[Task] {

  override def getCar(id: UUID): Task[Option[Car]] = storage.get(id).map(_.filterNot(_.status.isOccupied))

  override def availableCars(loc: Location): Task[Seq[Car]] =
    storage.listAll()
      .map(cars =>
        cars.filterNot(_.status.isOccupied)
          .sortBy(car => DistanceCalculator.calculateDistanceInKM(loc, car.location))
      )
}
