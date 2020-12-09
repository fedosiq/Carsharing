package com.fedosique.carsharing.logic

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.fedosique.carsharing.api.AdminApi
import com.fedosique.carsharing.storage.{InMemoryCarStorage, InMemoryUserStorage}
import monix.execution.Scheduler.Implicits.global

class AdminServiceModule(carStorage: InMemoryCarStorage, userStorage: InMemoryUserStorage) {
  private val service = new AdminServiceImpl(carStorage, userStorage)
  val routes: Route = pathPrefix("api" / "v1" / "admin") {
    new AdminApi(service).routes
  }
}
