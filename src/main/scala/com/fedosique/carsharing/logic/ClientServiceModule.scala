package com.fedosique.carsharing.logic

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.{Monad, ~>}
import com.fedosique.carsharing.api.ClientApi
import com.fedosique.carsharing.storage.{CarStorage, UserStorage}
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Future


class ClientServiceModule[DbEffect[_] : Monad](carStorage: CarStorage[DbEffect], userStorage: UserStorage[DbEffect])
                                              (implicit evalDb: DbEffect ~> Future) {
  private val service: ClientService[Future] = new ClientServiceGenericImpl(carStorage, userStorage)
  val routes: Route = pathPrefix("api" / "v1") {
    new ClientApi(service).routes
  }
}
