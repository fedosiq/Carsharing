package com.fedosique.carsharing.storage

import java.util.UUID

import com.fedosique.carsharing.{Car, Location, Status}
import org.scalatest.matchers.should.Matchers
import monix.execution.Scheduler.Implicits.global


class InMemoryCarStorageTest extends InMemoryCarStorageSuite with Matchers {

  behavior of "get"

  it should "return Some(Car) if car is in storage" in {
    storage.get(knownUUID).map(_ shouldBe Some(testCar)).runToFuture
  }

  it should "return None if car is NOT in storage" in {
    storage.get(UUID.randomUUID()).map(_ shouldBe None).runToFuture
  }


  behavior of "listAll"

  it should "return all stored cars" in {
    storage.listAll().map(_ shouldBe Seq(testCar)).runToFuture
  }

  it should "return empty sequence if storage is empty" in {
    (new InMemoryCarStorage).listAll().map(_ shouldBe Seq.empty).runToFuture
  }


  behavior of "put"

  it should "add order to the storage" in {
    val id = UUID.randomUUID()
    val newCar = Car("kia rio", "blue", "а117рп78", Location(60.787842, 55.848593), Status(1, isOccupied = false), 0)
    val test = for {
      _ <- storage.put(id, newCar)
      res <- storage.get(id)
    } yield assert(res.contains(newCar))
    test.runToFuture
  }


  behavior of "contains"

  it should "return true if the given id is present in storage" in {
    storage.contains(knownUUID).map(_ shouldBe true).runToFuture
  }

  it should "return false if the given id is NOT present in storage" in {
    storage.contains(UUID.randomUUID()).map(_ shouldBe false).runToFuture
  }
}
