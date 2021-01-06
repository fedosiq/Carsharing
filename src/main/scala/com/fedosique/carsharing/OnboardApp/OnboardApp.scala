package com.fedosique.carsharing.OnboardApp

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Source
import com.typesafe.scalalogging.LazyLogging

import java.util.UUID
import scala.concurrent.ExecutionContextExecutor

object OnboardApp extends LazyLogging {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher

  private val service = new OnboardService

  def main(args: Array[String]): Unit = {

    val thisCarId = UUID.fromString(args.head)

    import akka.http.scaladsl.unmarshalling.sse.EventStreamUnmarshalling._

    service.initState(thisCarId).flatMap(carState => {
      service.updateLoop(carState)

      Http()
        .singleRequest(Get("http://localhost:8080/api/v1/events"))
        .flatMap(Unmarshal(_).to[Source[ServerSentEvent, NotUsed]])
        .map(events => service.processEvents(events, carState))
    })
  }
}
