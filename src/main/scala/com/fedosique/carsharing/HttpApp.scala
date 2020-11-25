package com.fedosique.carsharing

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.fedosique.carsharing.api.ClientServiceRoutes
import com.fedosique.carsharing.logic.ClientServiceImpl
import com.fedosique.carsharing.storage.InMemoryCarStorage


object HttpApp extends App {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val ec = actorSystem.dispatcher

  val storage = new InMemoryCarStorage
  val service = new ClientServiceImpl(storage)
  val apiRoutes = pathPrefix("api" / "v1") {
    new ClientServiceRoutes(service).routes
  }

  Http()
    .newServerAt("localhost", 8080)
    .bind(apiRoutes)
    .foreach(s => println(s"server started at $s"))
}
