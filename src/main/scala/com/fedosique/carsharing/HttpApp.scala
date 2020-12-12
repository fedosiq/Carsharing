package com.fedosique.carsharing

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer
import cats.~>
import com.fedosique.carsharing.api.ApiModule
import com.fedosique.carsharing.logic.{AdminServiceModule, ClientServiceModule}
import com.fedosique.carsharing.storage.{InMemoryCarStorage, InMemoryUserStorage}
import monix.eval.Task

import scala.concurrent.Future


object HttpApp extends App {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer = Materializer(actorSystem)
  implicit val ec = actorSystem.dispatcher

  val carStorage = new InMemoryCarStorage
  val userStorage = new InMemoryUserStorage
  InMemoryCarStorage.init(carStorage)

  implicit private val evalDb = new (Task ~> Future) {
    override def apply[T](task: Task[T]): Future[T] =
      task.runToFuture(monix.execution.Scheduler.global)
  }

  private val clientServiceModule = new ClientServiceModule[Task](carStorage, userStorage)
  private val adminServiceModule = new AdminServiceModule[Task](carStorage, userStorage)

  private val apiModule = new ApiModule(clientServiceModule, adminServiceModule)

  Http()
    .newServerAt("localhost", 8080)
    .bind(apiModule.routes)
    .foreach(s => println(s"server started at $s"))
}
