package com.fedosique.carsharing.logic

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.fedosique.carsharing.api.ClientServiceRoutes
import com.fedosique.carsharing.storage.InMemoryCarStorage
import com.fedosique.carsharing.{Car, Location, Status}
import monix.eval.Task

import java.util.UUID

class ClientServiceModule(storage: InMemoryCarStorage) {

  private val kia = Car("kia rio", "blue", "а117рп78", Location(59.914412476005396, 30.318188229277073), Status(1, isOccupied = false), 0)
  private val bmw = Car("bmw 3", "black", "г651та78", Location(59.91876362948221, 30.31814575195313), Status(0.5, isOccupied = true), 0)

  private val initDB = for {
    _ <- storage.put(UUID.randomUUID(), kia)
    _ <- storage.put(UUID.randomUUID(), bmw)
  } yield ()
  initDB.runToFuture(monix.execution.Scheduler.Implicits.global)

  private val service: ClientService[Task] = new ClientServiceImpl(storage)
  val routes: Route = pathPrefix("api" / "v1") {
    new ClientServiceRoutes(service).routes
  }
}
