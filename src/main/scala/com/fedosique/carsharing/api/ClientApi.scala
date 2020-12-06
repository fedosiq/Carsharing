package com.fedosique.carsharing.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.fedosique.carsharing.Location
import com.fedosique.carsharing.logic.ClientService
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import java.util.UUID


class ClientApi(service: ClientService[Task]) {

  private val getCarById: Route = (get & path("cars" / JavaUUID)) { carId =>
    complete(service.getCar(carId).runToFuture)
  }
  private val availableCars: Route = (get & path("cars")) {
    parameters("lat".as[Double], "lon".as[Double]) { (lat, lon) =>
      complete(service.availableCars(Location(lat, lon)).runToFuture)
    }
  }
  private val occupyCar: Route = (post & path("cars" / JavaUUID / "occupy" )) { carId =>
    parameter("userId".as[UUID]) { userId =>
      complete(service.occupyCar(carId, userId).runToFuture)
    }
  }
  val routes: Route = getCarById ~ availableCars ~ occupyCar
}
