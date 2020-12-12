package com.fedosique.carsharing.OnboardApp

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Source
import io.circe.syntax.EncoderOps

import java.util.UUID
import scala.concurrent.ExecutionContextExecutor

object OnboardApp extends App {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher

  private val service = new OnboardService
  private val carApi = new OnboardApi(service)
  private val routes = carApi.route


  Http()
    .newServerAt("localhost", 8081)
    .bind(routes)
    .foreach(s => println(s"server started at $s"))

  import akka.http.scaladsl.unmarshalling.sse.EventStreamUnmarshalling._

  Http() //TODO: add retries
    .singleRequest(Get("http://localhost:8080/events"))
    .flatMap(Unmarshal(_).to[Source[ServerSentEvent, NotUsed]])
    .foreach(events =>
      events
        .filter(_.data.contains(service.thisCar.id.toString))
        .map(msg => msg.data.split(" ").toList match {

          case _ :: userId :: Nil if msg.eventType.contains("occupy") =>
            service.thisCar = service.thisCar.copy(status = service.thisCar.status.copy(isOccupied = true, occupiedBy = Some(UUID.fromString(userId))))
            println(service.thisCar.asJson)

          case _ :: _ :: Nil if msg.eventType.contains("leave") =>
            service.thisCar = service.thisCar.copy(status = service.thisCar.status.copy(isOccupied = false, occupiedBy = None))
            println(service.thisCar.asJson)

          case _ => println("Bad request")
        })
        .runForeach(println))

}
