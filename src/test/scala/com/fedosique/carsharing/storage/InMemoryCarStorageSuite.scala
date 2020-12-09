package com.fedosique.carsharing.storage

import java.util.UUID

import com.fedosique.carsharing.{Car, Location, Status}
import monix.execution.Scheduler.Implicits.global
import org.scalatest.flatspec.AsyncFlatSpec


abstract class InMemoryCarStorageSuite extends AsyncFlatSpec {
  val storage = new InMemoryCarStorage

  val knownUUID = UUID.randomUUID()
  val testCar = Car(knownUUID, "testCar", "green", "а117рп78", Location(60.787842, 55.848593), Status(1, isOccupied = false, None), 0)

  val initDB = for {
    _ <- storage.put(testCar)
  } yield ()
  initDB.runToFuture
}
