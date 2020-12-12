package com.fedosique.carsharing.logic

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.~>
import com.fedosique.carsharing.api.ClientApi

import scala.concurrent.Future


class ClientServiceModule[F[_]](clientService: ClientService[F])(implicit FK: F ~> Future) {
  val routes: Route = pathPrefix("api" / "v1") {
    new ClientApi(clientService).routes
  }
}
