package com.fedosique.carsharing

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer
import cats.implicits.catsStdInstancesForFuture
import cats.~>
import com.fedosique.carsharing.api.ApiModule
import com.fedosique.carsharing.logic._
import com.fedosique.carsharing.storage.{InMemoryCarStorage, InMemoryUserStorage}
import monix.eval.Task

import scala.concurrent.Future


//TODO: delete
object OldHttpApp extends App {

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
  implicit private lazy val FK: Future ~> Future = new (Future ~> Future) {
    override def apply[T](f: Future[T]): Future[T] = f
  }

  private val clientService = new ClientServiceGenericImpl[Future, Task](carStorage, userStorage)
  private val adminService = new AdminServiceGenericImpl[Future, Task](carStorage, userStorage)
  private val carService = new CarServiceImpl

  private val clientServiceModule = new ClientServiceModule(clientService)
  private val adminServiceModule = new AdminServiceModule(adminService)
  private val carServiceModule = new CarServiceModule(carService)

  private val apiModule = new ApiModule(clientServiceModule, adminServiceModule, carServiceModule)

  Http()
    .newServerAt("localhost", 8080)
    .bind(apiModule.routes)
    .foreach(s => println(s"server started at $s"))
}
