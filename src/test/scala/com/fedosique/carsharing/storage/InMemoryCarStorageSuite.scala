package com.fedosique.carsharing.storage

import java.util.UUID

import com.fedosique.carsharing.Car
import monix.execution.Scheduler.Implicits.global
import org.scalatest.flatspec.AsyncFlatSpec


abstract class InMemoryCarStorageSuite extends AsyncFlatSpec{
  val storage = new InMemoryCarStorage

  val testCar = Car("testCar", "green", "а117рп78", 60.787842, 55.848593, 1, isOccupied = false)
  val knownUUID = UUID.randomUUID()

  val initDB = for {
    _ <- storage.put(knownUUID, testCar)
  } yield ()
  initDB.runToFuture
}
