package com.fedosique.carsharing.api

import akka.http.scaladsl.server.{Route, RouteConcatenation}
import com.fedosique.carsharing.logic.{AdminServiceModule, ClientServiceModule}


class ApiModule[DbEffect[_]](clientServiceModule: ClientServiceModule[DbEffect], adminServiceModule: AdminServiceModule[DbEffect]) {
  val carServiceApi = new CarServiceApi
  val routes: Route = Route.seal(
    RouteConcatenation.concat(
      clientServiceModule.routes,
      adminServiceModule.routes,
      carServiceApi.routes
    )
  )(exceptionHandler = CarsharingExceptionHandler.exceptionHandler)
}
