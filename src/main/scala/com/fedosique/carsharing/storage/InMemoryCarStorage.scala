package com.fedosique.carsharing.storage

import com.fedosique.carsharing.Car
import monix.eval.Task

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import scala.jdk.CollectionConverters._


class InMemoryCarStorage extends CarStorage[Task] {

  override def put(id: UUID, car: Car): Task[Unit] = Task(storage.put(id, car))

  override def get(id: UUID): Task[Option[Car]] = Task {
    if (storage.containsKey(id)) Some(storage.get(id))
    else None
  }

  override def listAll(): Task[Seq[Car]] = Task(storage.values.asScala.toSeq)

  override def contains(id: UUID): Task[Boolean] = Task(storage.containsKey(id))

  private val storage = new ConcurrentHashMap[UUID, Car]
}
