package com.fedosique.carsharing.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.fedosique.carsharing.Location
import com.fedosique.carsharing.logic.ClientService
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._

import java.util.UUID
import scala.concurrent.Future


class ClientApi(service: ClientService[Future]) {

  private val getCarById: Route = (get & path("cars" / JavaUUID)) { carId =>
    complete(service.getCar(carId))
  }
  private val availableCars: Route = (get & path("cars")) {
    parameters("lat".as[Double], "lon".as[Double]) { (lat, lon) =>
      complete(service.availableCars(Location(lat, lon)))
    }
  }
  private val occupyCar: Route = (post & path("cars" / JavaUUID / "occupy")) { carId =>
    parameter("userId".as[UUID]) { userId =>
      complete(service.occupyCar(carId, userId))
    }
  }
  private val leaveCar: Route = (post & path("cars" / JavaUUID / "leave")) { carId =>
    parameter("userId".as[UUID]) { userId =>
      complete(service.leaveCar(carId, userId))
    }
  }

  val routes: Route = getCarById ~ availableCars ~ occupyCar ~ leaveCar
}
