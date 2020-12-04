package com.fedosique.carsharing

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Route, RouteConcatenation}
import com.fedosique.carsharing.logic.{AdminServiceModule, ClientServiceModule}
import com.fedosique.carsharing.storage.InMemoryCarStorage


object HttpApp extends App {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val ec = actorSystem.dispatcher

  val storage = new InMemoryCarStorage
  InMemoryCarStorage.init(storage)
  private val clientServiceModule = new ClientServiceModule(storage)
  private val adminServiceModule = new AdminServiceModule(storage)


  // мб положить это в какой-нибудь ApiModule(clientServiceModule, adminServiceModule)?
  private val routes = Route.seal(
    RouteConcatenation.concat(
      clientServiceModule.routes,
      adminServiceModule.routes
    )
  )

  Http()
    .newServerAt("localhost", 8080)
    .bind(routes)
    .foreach(s => println(s"server started at $s"))
}
