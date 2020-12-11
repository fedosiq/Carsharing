package com.fedosique.carsharing.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.fedosique.carsharing.Car
import com.fedosique.carsharing.logic.AdminService
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._

import scala.concurrent.Future


class AdminApi(service: AdminService[Future]) {

  private val getCarById: Route = (get & path("cars" / JavaUUID)) { carId =>
    complete(service.getCar(carId))
  }
  private val addCar: Route = (post & path("cars" / "add")) {
    entity(as[Car]) { car =>
      complete(service.addCar(car))
    }
  }
  private val updateCar: Route = (post & path("cars" / "update")) {
    entity(as[Car]) { car =>
      complete(service.updateCar(car))
    }
  }
  private val allCars: Route = (get & path("cars")) {
    complete(service.cars)
  }
  private val addUser: Route = (post & path("users" / "add")) {
    parameters("name".as[String], "email".as[String]) { (name, email) =>
      complete(service.addUser(name, email))
    }
  }
  private val getUserById: Route = (get & path("users" / JavaUUID)) { userID =>
    complete(service.getUser(userID))
  }

  val routes: Route = getCarById ~ addCar ~ updateCar ~ allCars ~ addUser ~ getUserById
}
