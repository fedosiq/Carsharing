package com.fedosique.carsharing.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.~>
import com.fedosique.carsharing.logic.ClientService
import com.fedosique.carsharing.model.Location
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._

import java.util.UUID
import scala.concurrent.Future


class ClientApi[F[_]](service: ClientService[F])(implicit FK: F ~> Future) {

  private val getCarById: Route = (get & path("cars" / JavaUUID)) { carId =>
    complete(FK(service.getCar(carId)))
  }
  private val availableCars: Route = (get & path("cars")) {
    parameters("lat".as[Double], "lon".as[Double], "limit".as[Int]) { (lat, lon, limit) =>
      complete(FK(service.availableCars(Location(lat, lon), limit)))
    }
  }
  private val occupyCar: Route = (post & path("cars" / JavaUUID / "occupy")) { carId =>
    parameter("userId".as[UUID]) { userId =>
      complete(FK(service.occupyCar(carId, userId)))
    }
  }
  private val leaveCar: Route = (post & path("cars" / JavaUUID / "leave")) { carId =>
    parameter("userId".as[UUID]) { userId =>
      complete(FK(service.leaveCar(carId, userId)))
    }
  }

  val routes: Route = getCarById ~ availableCars ~ occupyCar ~ leaveCar
}
