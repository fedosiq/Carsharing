package com.fedosique.carsharing.logic

import java.util.UUID

import com.fedosique.carsharing.Car
import com.fedosique.carsharing.storage.CarStorage
import monix.eval.Task

class AdminServiceImpl(storage: CarStorage[Task]) extends AdminService[Task] {

  override def getCar(id: UUID): Task[Option[Car]] = storage.get(id)

  override def addCar(car: Car): Task[UUID] = {
    val id = UUID.randomUUID()
    storage.put(id, car).flatMap(_ => Task.now(id))
  }

  def cars: Task[Seq[Car]] = storage.listAll()
}
