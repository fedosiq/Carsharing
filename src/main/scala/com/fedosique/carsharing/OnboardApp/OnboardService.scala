package com.fedosique.carsharing.OnboardApp

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.stream.scaladsl.Source
import com.fedosique.carsharing.model.Car
import com.typesafe.scalalogging.StrictLogging
import io.circe.syntax.EncoderOps
import io.circe.{ParsingFailure, parser}

import java.util.UUID
import java.util.concurrent.atomic.AtomicReference
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}


class OnboardService(implicit actorSystem: ActorSystem, ec: ExecutionContext) extends StrictLogging {

  def initState(id: UUID): Future[AtomicReference[Car]] = {
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = s"http://localhost:8080/api/v1/admin/cars/$id"  // TODO: cars should not send requests to admin service
    )
    Http()
      .singleRequest(request)
      .flatMap(_.entity.toStrict(2.seconds))
      .map(resp => parser.decode[Car](resp.data.utf8String) match {
        case Right(car) => new AtomicReference[Car](car)
        case Left(error) => throw ParsingFailure(error.getMessage, error)
      })
  }

  def processEvents(events: Source[ServerSentEvent, NotUsed], carState: AtomicReference[Car]) =
    events
      .filter(_.data.contains(carState.get().id.toString))
      .map(msg => msg.data.split(" ").toList match {

        case List(_, userId) if msg.eventType.contains("occupy") =>
          val currentState = carState.get()
          carState.set(currentState.copy(status = currentState.status.copy(isOccupied = true, occupiedBy = Some(UUID.fromString(userId)))))
          logger.warn(s"Car occupied, current state: ${carState.get().asJson.toString}")

        case List(_, _) if msg.eventType.contains("leave") =>
          val currentState = carState.get()
          carState.set(currentState.copy(status = currentState.status.copy(isOccupied = false, occupiedBy = None)))
          logger.warn(s"Car left, current state: ${carState.get().asJson.toString}")

        case _ => logger.error("Bad request: No event type provided")
      })
      .runForeach(println)

  def updateLoop(carState: AtomicReference[Car]): Future[Car] = {
    Thread.sleep(10000)
    //change car state
    sendUpdate(carState.get()).flatMap(_ => updateLoop(carState))
  }

  def sendUpdate(car: Car): Future[Car] = {
    logger.info(s"Sent car state: ${car.toString}")
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
      .map(resp => parser.decode[Car](resp.data.utf8String) match {
        case Right(car) => car
        case Left(error) => throw ParsingFailure(error.getMessage, error)
      })
  }
}
