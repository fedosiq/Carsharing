package com.fedosique.carsharing.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.fedosique.carsharing.Car
import com.fedosique.carsharing.logic.AdminService
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global


class AdminApi(service: AdminService[Task]) {

  private val getCarById: Route = (get & path("cars" / JavaUUID)) { carId =>
    complete(service.getCar(carId).runToFuture)
  }
  private val addCar: Route = (post & path("cars" / "add")) {
    entity(as[Car]) { car =>
      complete(service.addCar(car).runToFuture)
    }
  }
  private val allCars: Route = (get & path("cars")) {
    complete(service.cars.runToFuture)
  }

  val routes: Route = getCarById ~ addCar ~ allCars
}
