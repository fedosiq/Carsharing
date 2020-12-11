package com.fedosique.carsharing.api

import akka.actor.ActorSystem
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Source, SourceQueueWithComplete}
import akka.stream.{ActorMaterializer, OverflowStrategy}

import scala.concurrent.duration.DurationInt

class CarServiceApi {

  import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val sourceDecl: Source[String, SourceQueueWithComplete[String]] = Source.queue[String](bufferSize = 100, OverflowStrategy.dropHead)
  val (sourceMat, source) = sourceDecl.preMaterialize()

  val eventSource = source.map(ServerSentEvent(_)).keepAlive(1.second, () => ServerSentEvent.heartbeat)

  sourceMat.offer("occupy")
  sourceMat.offer("leave")

  private val events: Route =
    (get & path("events")) {
      complete {
        eventSource
      }
    } ~ (post & path("events")) {
      entity(as[String]) { event =>
        complete {
          sourceMat.offer(event)
          eventSource
        }
      }
    }


  val routes: Route = events

}
