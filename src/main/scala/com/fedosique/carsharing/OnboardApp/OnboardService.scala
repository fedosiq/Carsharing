package com.fedosique.carsharing.OnboardApp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import com.fedosique.carsharing.Car
import io.circe.jackson.parse
import io.circe.syntax.EncoderOps

import scala.concurrent.duration.DurationInt


class OnboardService {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val ec = actorSystem.dispatcher

  def sendUpdate(car: Car) = {
    println(car)
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = "http://localhost:8080/api/v1/admin/cars/update",
      entity = HttpEntity(
        ContentTypes.`application/json`,
        s"${car.asJson}"
      )
    )
    Http()
      .singleRequest(request)
      .flatMap(_.entity.toStrict(2.seconds))
      .map(resp => parse(resp.data.utf8String))
  }
}
