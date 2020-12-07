package com.fedosique.carsharing.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.fedosique.carsharing.Car
import com.fedosique.carsharing.logic.AdminService
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.ExecutionContext


class AdminApi(service: AdminService[Task])(implicit ec: ExecutionContext) {

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
  private val addUser: Route = (post & path("users" / "add")) {
    parameters("name".as[String], "email".as[String]) { (name, email) =>
      complete(service.addUser(name, email).runToFuture)
    }
  }
  private val getUserById: Route = (get & path("users" / JavaUUID)) { userID =>
    complete(service.getUser(userID).runToFuture)
  }

  val routes: Route = getCarById ~ addCar ~ allCars ~ addUser ~ getUserById
}
