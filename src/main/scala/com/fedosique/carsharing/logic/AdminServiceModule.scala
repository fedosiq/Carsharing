package com.fedosique.carsharing.logic

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.~>
import com.fedosique.carsharing.api.AdminApi

import scala.concurrent.Future


class AdminServiceModule[F[_]](adminService: AdminService[F])(implicit FK: F ~> Future) {
  val routes: Route = pathPrefix("api" / "v1" / "admin") {
    new AdminApi(adminService).routes
  }
}
