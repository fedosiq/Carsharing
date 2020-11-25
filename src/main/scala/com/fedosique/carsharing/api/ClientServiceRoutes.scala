package com.fedosique.carsharing.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.fedosique.carsharing.Car
import com.fedosique.carsharing.logic.ClientService
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global


class ClientServiceRoutes(service: ClientService[Task]) {

  private val getCarById: Route = (get & path("cars" / IntNumber)) { carId =>
    complete(service.getCar(carId).runToFuture)
  }
  private val listAllCars: Route = (get & path("cars")) {
    complete(service.carList.runToFuture)
  }
  private val addCar: Route = (post & path("cars" / "add")) {
    entity(as[Car]) { car =>
      complete(service.addCar(car).runToFuture)
    }
  }

  val routes: Route = getCarById ~ listAllCars ~ addCar
}
