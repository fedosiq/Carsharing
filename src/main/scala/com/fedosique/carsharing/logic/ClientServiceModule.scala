package com.fedosique.carsharing.logic

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.fedosique.carsharing.api.ClientApi
import com.fedosique.carsharing.storage.InMemoryCarStorage
import monix.eval.Task

class ClientServiceModule(storage: InMemoryCarStorage) {
  private val service: ClientService[Task] = new ClientServiceImpl(storage)
  val routes: Route = pathPrefix("api" / "v1") {
    new ClientApi(service).routes
  }
}
