package com.fedosique.carsharing.api

import akka.http.scaladsl.server.{Route, RouteConcatenation}
import akka.stream.Materializer
import com.fedosique.carsharing.logic.{AdminServiceModule, CarServiceModule, ClientServiceModule}


class ApiModule[F[_]](clientServiceModule: ClientServiceModule[F], adminServiceModule: AdminServiceModule[F], carServiceModule: CarServiceModule)
                            (implicit materializer: Materializer) {
  val routes: Route = Route.seal(
    RouteConcatenation.concat(
      clientServiceModule.routes,
      adminServiceModule.routes,
      carServiceModule.routes
    )
  )(exceptionHandler = CarsharingExceptionHandler.exceptionHandler)
}
