package com.fedosique.carsharing.storage

import com.fedosique.carsharing.{Car, Location, Status}
import monix.eval.Task

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import scala.concurrent.Future
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

object InMemoryCarStorage {
  private val sampleCars: List[Car] = List(
    Car("kia rio", "blue", "а117рп78", Location(59.914412476005396, 30.318188229277073), Status(1, isOccupied = false), 0),
    Car("bmw 3", "black", "г651та78", Location(59.91876362948221, 30.31814575195313), Status(0.5, isOccupied = true), 0)
  )

  def init(storage: InMemoryCarStorage): Future[Unit] =
    (for {
      _ <- Task.sequence(sampleCars.map(storage.put(UUID.randomUUID(), _)))
    } yield ()).runToFuture(monix.execution.Scheduler.Implicits.global)
}
