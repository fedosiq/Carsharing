package com.fedosique.carsharing.logic

import akka.http.scaladsl.server.Directives.pathPrefix
import com.fedosique.carsharing.api.AdminApi
import com.fedosique.carsharing.storage.InMemoryCarStorage
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

class AdminServiceModule(storage: InMemoryCarStorage) {
  private val service = new AdminServiceImpl(storage)
  val routes: Route = pathPrefix("api" / "v1" / "admin") {
    new AdminApi(service).routes
  }
}
