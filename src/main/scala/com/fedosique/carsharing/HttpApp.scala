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
  val kia = Car("kia rio", "blue", "а117рп78", 60.787842, 55.848593, 1, isOccupied = false)
  val bmw = Car("bmw 3", "black", "г651та78", 60.787842, 70.342421, 0.5, isOccupied = true)

  val initDB = for {
    _ <- storage.put(1, kia)
    _ <- storage.put(2, bmw)
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
