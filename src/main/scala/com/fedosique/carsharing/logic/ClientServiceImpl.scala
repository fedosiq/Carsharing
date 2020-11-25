package com.fedosique.carsharing.logic

import com.fedosique.carsharing.Car
import com.fedosique.carsharing.storage.CarStorage
import monix.eval.Task

import scala.util.Random


class ClientServiceImpl(storage: CarStorage[Task]) extends ClientService[Task] {

  override def addCar(car: Car): Task[Int] = {
    val id = Random.nextInt(1000000)
    storage.contains(id).flatMap {
      case false => storage.put(id, car).flatMap(_ => Task.now(id))
      case _ => addCar(car)
    }
  }

  override def getCar(id: Int): Task[Option[Car]] = storage.get(id)

  override def carList: Task[Seq[Car]] = storage.listAll()
}
