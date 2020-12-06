package com.fedosique.carsharing.storage

import java.util.UUID

import com.fedosique.carsharing.{Car, Location, Status}
import monix.execution.Scheduler.Implicits.global
import org.scalatest.flatspec.AsyncFlatSpec


abstract class InMemoryCarStorageSuite extends AsyncFlatSpec {
  val storage = new InMemoryCarStorage

  val testCar = Car("testCar", "green", "а117рп78", Location(60.787842, 55.848593), Status(1, isOccupied = false, None), 0)
  val knownUUID = UUID.randomUUID()

  val initDB = for {
    _ <- storage.put(knownUUID, testCar)
  } yield ()
  initDB.runToFuture
}
