package com.fedosique.carsharing.api

import akka.http.scaladsl.server.{Route, RouteConcatenation}
import com.fedosique.carsharing.logic.{AdminServiceModule, ClientServiceModule}

class ApiModule(clientServiceModule: ClientServiceModule, adminServiceModule: AdminServiceModule) {
  val routes: Route = Route.seal(
    RouteConcatenation.concat(
      clientServiceModule.routes,
      adminServiceModule.routes
    )
  )
}
