package com.fedosique.carsharing

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import cats.~>
import com.fedosique.carsharing.api.ApiModule
import com.fedosique.carsharing.logic._
import com.fedosique.carsharing.storage._
import com.rms.miu.slickcats.DBIOInstances._
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.Future


object CoolHttpApp extends App {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val ec = actorSystem.dispatcher

  val db = Database.forConfig("db")
  implicit private lazy val evalDb: DBIO ~> Future = new (DBIO ~> Future) {
    override def apply[T](dbio: DBIO[T]): Future[T] = db.run(dbio)
  }

  private val carStorage = new SlickCarStorage
  private val userStorage = new SlickUserStorage

  private val clientServiceModule = new ClientServiceModule[DBIO](carStorage, userStorage)
  private val adminServiceModule = new AdminServiceModule[DBIO](carStorage, userStorage)

  private val apiModule = new ApiModule(clientServiceModule, adminServiceModule)

  Http()
    .newServerAt("localhost", 8080)
    .bind(apiModule.routes)
    .foreach(s => println(s"server started at $s"))
}
