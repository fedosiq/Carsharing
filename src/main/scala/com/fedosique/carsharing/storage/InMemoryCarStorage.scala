package com.fedosique.carsharing.storage

import com.fedosique.carsharing.Car
import monix.eval.Task

import scala.collection.concurrent.TrieMap


class InMemoryCarStorage extends CarStorage[Task] {

  override def put(id: Int, car: Car): Task[Unit] = Task(storage.put(id, car))

  override def get(id: Int): Task[Option[Car]] = Task(storage.get(id))

  override def listAll(): Task[Seq[Car]] = Task(storage.values.toSeq)

  override def contains(id: Int): Task[Boolean] = Task(storage.contains(id))


  private val storage = new TrieMap[Int, Car]

  val kia = Car("kia rio", "blue", "а117рп78", 60.787842, 55.848593, 1, isOccupied = false)
  val bmw = Car("bmw 3", "black", "г651та78", 60.787842, 70.342421, 0.5, isOccupied = true)
  storage ++= Seq(1 -> kia, 2 -> bmw)
}
