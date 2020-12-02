package com.fedosique.carsharing

import java.util.UUID

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
  val kia = Car("kia rio", "blue", "а117рп78", Location(59.914412476005396, 30.318188229277073), Status(1, isOccupied = false), 0)
  val bmw = Car("bmw 3", "black", "г651та78", Location(59.91876362948221, 30.31814575195313), Status(0.5, isOccupied = true), 0)

  val initDB = for {
    _ <- storage.put(UUID.randomUUID(), kia)
    _ <- storage.put(UUID.randomUUID(), bmw)
  } yield ()
  initDB.runToFuture(monix.execution.Scheduler.Implicits.global)

  val service = new ClientServiceImpl(storage)
  val apiRoutes = pathPrefix("api" / "v1") {
    new ClientServiceRoutes(service).routes
  }

  Http()
    .newServerAt("localhost", 8080)
    .bind(apiRoutes)
    .foreach(s => println(s"server started at $s"))
}
