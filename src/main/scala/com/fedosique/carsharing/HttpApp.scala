package com.fedosique.carsharing

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.fedosique.carsharing.api.ApiModule
import com.fedosique.carsharing.logic.{AdminServiceModule, ClientServiceModule}
import com.fedosique.carsharing.storage.InMemoryCarStorage


object HttpApp extends App {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val ec = actorSystem.dispatcher

  val storage = new InMemoryCarStorage
  InMemoryCarStorage.init(storage)

  private val clientServiceModule = new ClientServiceModule(storage)
  private val adminServiceModule = new AdminServiceModule(storage)

  private val apiModule = new ApiModule(clientServiceModule, adminServiceModule)

  Http()
    .newServerAt("localhost", 8080)
    .bind(apiModule.routes)
    .foreach(s => println(s"server started at $s"))
}
