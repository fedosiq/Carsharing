package com.fedosique.carsharing.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.fedosique.carsharing.Location
import com.fedosique.carsharing.logic.ClientService
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global


class ClientServiceRoutes(service: ClientService[Task]) {

  private val getCarById: Route = (get & path("cars" / JavaUUID)) { carId =>
    complete(service.getCar(carId).runToFuture)
  }
  private val freeCars: Route = (get & path("cars")) {
    parameters("lat".as[Double], "lon".as[Double]) { (lat, lon) =>
      complete(service.freeCars(Location(lat, lon)).runToFuture)
    }
  }

  val routes: Route = getCarById ~ freeCars
}
