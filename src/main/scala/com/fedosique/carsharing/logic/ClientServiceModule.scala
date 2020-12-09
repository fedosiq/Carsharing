package com.fedosique.carsharing.logic

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.fedosique.carsharing.api.ClientApi
import com.fedosique.carsharing.storage.{InMemoryCarStorage, InMemoryUserStorage}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

class ClientServiceModule(carStorage: InMemoryCarStorage, userStorage: InMemoryUserStorage) {
  private val service: ClientService[Task] = new ClientServiceImpl(carStorage, userStorage)
  val routes: Route = pathPrefix("api" / "v1") {
    new ClientApi(service).routes
  }
}
