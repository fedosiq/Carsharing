package com.fedosique.carsharing.logic

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.fedosique.carsharing.api.CarServiceApi


class CarServiceModule(carService: CarServiceImpl) {
  val routes: Route = pathPrefix("api" / "v1") {
    new CarServiceApi(carService).routes
  }
}
