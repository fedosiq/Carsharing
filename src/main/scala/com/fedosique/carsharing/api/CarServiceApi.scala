package com.fedosique.carsharing.api

import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.fedosique.carsharing.api.ServerSentEventCodec.{sseDecoder, sseEncoder}
import com.fedosique.carsharing.logic.CarServiceImpl
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder}


/**
 * Служит для отправки сообщений на машины
 * */
class CarServiceApi(carService: CarServiceImpl){

  import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._

  private val events: Route = {
    (get & path("events")) {
      complete {
        carService.eventSource
        //  .filterNot(_.data.contains("ping"))
      }
    } ~
      (post & path("events")) {
        entity(as[ServerSentEvent]) { event =>
          complete {
            carService.queue.offer(event)
            s"pushed ${event.asJson} to events queue" //TODO: change to json response?
          }
        }
      }
  }

  val routes: Route = events
}

object ServerSentEventCodec {
  implicit val sseDecoder: Decoder[ServerSentEvent] = deriveDecoder
  implicit val sseEncoder: Encoder[ServerSentEvent] = deriveEncoder
}
