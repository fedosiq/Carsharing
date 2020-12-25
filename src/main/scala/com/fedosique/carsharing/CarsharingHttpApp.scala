package com.fedosique.carsharing

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer
import cats.implicits.catsStdInstancesForFuture
import cats.~>
import com.fedosique.carsharing.api.ApiModule
import com.fedosique.carsharing.logic._
import com.fedosique.carsharing.storage._
import com.rms.miu.slickcats.DBIOInstances._
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.Future


object CarsharingHttpApp extends App {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer = Materializer(actorSystem)
  implicit val ec = actorSystem.dispatcher

  val db = Database.forConfig("db")
  implicit private lazy val evalDb: DBIO ~> Future = new (DBIO ~> Future) {
    override def apply[T](dbio: DBIO[T]): Future[T] = db.run(dbio)
  }
  implicit private lazy val FK: Future ~> Future = new (Future ~> Future) {
    override def apply[T](f: Future[T]): Future[T] = f
  }

  private val carStorage = new SlickCarStorage
  private val userStorage = new SlickUserStorage
  private val eventStorage = new SlickEventStorage

  private val clientService = new ClientServiceGenericImpl[Future, DBIO](carStorage, userStorage, eventStorage)
  private val adminService = new AdminServiceGenericImpl[Future, DBIO](carStorage, userStorage)
  private val carService = new CarServiceImpl

  private val clientServiceModule = new ClientServiceModule(clientService)
  private val adminServiceModule = new AdminServiceModule(adminService)
  private val carServiceModule = new CarServiceModule(carService)

  private val apiModule = new ApiModule(clientServiceModule, adminServiceModule, carServiceModule)

  Http()
    .newServerAt("0.0.0.0", 8080)
    .bind(apiModule.routes)
    .foreach(s => println(s"server started at $s"))
}
