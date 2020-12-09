package com.fedosique.carsharing.logic

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.implicits.catsStdInstancesForFuture
import cats.{Monad, ~>}
import com.fedosique.carsharing.api.AdminApi
import com.fedosique.carsharing.storage.{CarStorage, UserStorage}
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Future


class AdminServiceModule[DbEffect[_] : Monad](carStorage: CarStorage[DbEffect], userStorage: UserStorage[DbEffect])
                                             (implicit evalDb: DbEffect ~> Future) {
  private val service = new AdminServiceGenericImpl[Future, DbEffect](carStorage, userStorage)
  val routes: Route = pathPrefix("api" / "v1" / "admin") {
    new AdminApi(service).routes
  }
}
