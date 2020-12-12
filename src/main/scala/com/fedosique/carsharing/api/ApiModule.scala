package com.fedosique.carsharing.api

import akka.http.scaladsl.server.{Route, RouteConcatenation}
import akka.stream.Materializer
import com.fedosique.carsharing.logic.{AdminServiceModule, CarServiceImpl, ClientServiceModule}


class ApiModule[DbEffect[_]](clientServiceModule: ClientServiceModule[DbEffect], adminServiceModule: AdminServiceModule[DbEffect])
                            (implicit materializer: Materializer) {
  val carServiceApi = new CarServiceApi(new CarServiceImpl)
  val routes: Route = Route.seal(
    RouteConcatenation.concat(
      clientServiceModule.routes,
      adminServiceModule.routes,
      carServiceApi.routes
    )
  )(exceptionHandler = CarsharingExceptionHandler.exceptionHandler)
}
