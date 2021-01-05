package com.fedosique.carsharing.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.~>
import com.fedosique.carsharing.logic.AdminService
import com.fedosique.carsharing.model.Car
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._

import scala.concurrent.Future


class AdminApi[F[_]](service: AdminService[F])(implicit FK: F ~> Future) {

  private val getCarById: Route = (get & path("cars" / JavaUUID)) { carId =>
    complete(FK(service.getCar(carId)))
  }
  private val addCar: Route = (post & path("cars" / "add")) {
    entity(as[Car]) { car =>
      complete(FK(service.addCar(car)))
    }
  }
  private val updateCar: Route = (post & path("cars" / "update")) {
    entity(as[Car]) { car =>
      complete(FK(service.updateCar(car)))
    }
  }
  private val allCars: Route = (get & path("cars")) {
    parameter("limit".as[Int]) { limit =>
      complete(FK(service.cars(limit)))
    }
  }
  private val addUser: Route = (post & path("users" / "add")) {
    parameters("name".as[String], "email".as[String]) { (name, email) =>
      complete(FK(service.addUser(name, email)))
    }
  }
  private val getUserById: Route = (get & path("users" / JavaUUID)) { userID =>
    complete(FK(service.getUser(userID)))
  }

  val routes: Route = getCarById ~ addCar ~ updateCar ~ allCars ~ addUser ~ getUserById
}
