package com.fedosique.carsharing.OnboardApp

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.fedosique.carsharing.models.Car
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._

/**
 * Использовался для дебага
 * */
class OnboardApi(service: OnboardService) {
  private val sendUpdate: Route = (post & path("api" / "v1" / "update")) {
    entity(as[Car]) { carInfo =>
      complete(service.sendUpdate(carInfo))
    }
  }
//  private val sendCurrentState: Route = (post & path("api" / "v1" / "update")) {
//    complete(service.sync)
//  }

  val route: Route = sendUpdate
}
