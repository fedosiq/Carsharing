package com.fedosique.carsharing.storage

import java.util.UUID

import com.fedosique.carsharing.Car
import monix.eval.Task

import scala.collection.concurrent.TrieMap


class InMemoryCarStorage extends CarStorage[Task] {

  override def put(id: UUID, car: Car): Task[Unit] = Task(storage.put(id, car))

  override def get(id: UUID): Task[Option[Car]] = Task(storage.get(id))

  override def listAll(): Task[Seq[Car]] = Task(storage.values.toSeq)

  override def contains(id: UUID): Task[Boolean] = Task(storage.contains(id))

  private val storage = new TrieMap[UUID, Car]
}
