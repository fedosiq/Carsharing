package com.fedosique.carsharing.OnboardApp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import com.fedosique.carsharing.{Car, Location, Status}
import io.circe.jackson.parse
import io.circe.syntax.EncoderOps

import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt


class OnboardService(implicit actorSystem: ActorSystem, ec: ExecutionContext) {

  var thisCar = Car(UUID.fromString("00000000-0000-0000-0000-100000000001"), "kia rio", "blue", "а117рп78",
    Location(59.914412476005396, 30.318188229277073), Status(1, isOccupied = false, None), 0)

  //TODO: добавить установку состояния и отделить от отправки состояния
  def sendUpdate(car: Car) = {
    println(car)
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = "http://localhost:8080/api/v1/admin/cars/update",
      entity = HttpEntity(
        ContentTypes.`application/json`,
        s"${car.asJson.noSpaces}"
      )
    )
    Http()
      .singleRequest(request)
      .flatMap(_.entity.toStrict(2.seconds))
      .map(resp => parse(resp.data.utf8String))
  }
  def sync = sendUpdate(thisCar)
}
