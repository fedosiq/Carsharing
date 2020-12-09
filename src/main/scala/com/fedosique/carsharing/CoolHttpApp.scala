package com.fedosique.carsharing

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, RouteConcatenation}
import cats.implicits.catsStdInstancesForFuture
import cats.~>
import com.fedosique.carsharing.api.{AdminApi, CarsharingExceptionHandler, ClientApi}
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

  // TODO: написать обобщенные модули
  // TODO: перенести в модули
  private val adminService: AdminService[Future] = new AdminServiceGenericImpl[Future, DBIO](carStorage, userStorage)
  private val clientService: ClientService[Future] = new ClientServiceGenericImpl[Future, DBIO](carStorage, userStorage)


  val clientRoutes: Route = pathPrefix("api" / "v1") {
    new ClientApi(clientService).routes
  }
  val adminRoutes: Route = pathPrefix("api" / "v1" / "admin") {
    new AdminApi(adminService).routes
  }
  val routes: Route = Route.seal(
    RouteConcatenation.concat(
      clientRoutes,
      adminRoutes
    )
  )(exceptionHandler = CarsharingExceptionHandler.exceptionHandler)

  Http()
    .newServerAt("localhost", 8080)
    .bind(routes)
    .foreach(s => println(s"server started at $s"))
}
