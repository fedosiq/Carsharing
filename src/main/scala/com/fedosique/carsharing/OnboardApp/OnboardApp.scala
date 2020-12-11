package com.fedosique.carsharing.OnboardApp

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Source

import scala.concurrent.ExecutionContextExecutor

object OnboardApp extends App {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher

  val service = new OnboardService
  val carApi = new OnboardApi(service)
  val routes = carApi.route


  Http()
    .newServerAt("localhost", 8081)
    .bind(routes)
    .foreach(s => println(s"server started at $s"))


  import akka.http.scaladsl.unmarshalling.sse.EventStreamUnmarshalling._

  Http()
    .singleRequest(Get("http://localhost:8080/events"))
    .flatMap(Unmarshal(_).to[Source[ServerSentEvent, NotUsed]])
    .foreach(_.runForeach(println))

}
