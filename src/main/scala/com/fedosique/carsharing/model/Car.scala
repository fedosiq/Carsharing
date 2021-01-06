package com.fedosique.carsharing.model

import io.circe._
import io.circe.generic.semiauto._

import java.time.Instant
import java.util.UUID


final case class Car(id: UUID,
                     name: String,
                     color: String,
                     plateNumber: String,
                     location: Location,
                     status: Status,
                     price: Double) {
  def isOccupied: Boolean = status.occupiedBy.isDefined
}

final case class Status(fuel: Double, occupiedBy: Option[UUID], lastUpdate: Instant)

object Car {
  implicit val jsonDecoder: Decoder[Car] = deriveDecoder
  implicit val jsonEncoder: Encoder[Car] = deriveEncoder
}

object Status {
  implicit val jsonDecoder: Decoder[Status] = deriveDecoder
  implicit val jsonEncoder: Encoder[Status] = deriveEncoder
}
