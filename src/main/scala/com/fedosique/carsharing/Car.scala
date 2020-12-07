package com.fedosique.carsharing

import io.circe._
import io.circe.generic.semiauto._

import java.util.UUID


final case class Car(id: UUID,
                     name: String,
                     color: String,
                     plateNumber: String,
                     location: Location,
                     status: Status,
                     price: Double)

final case class Status(fuel: Double, isOccupied: Boolean, occupiedBy: Option[UUID])

object Car {
  implicit val jsonDecoder: Decoder[Car] = deriveDecoder
  implicit val jsonEncoder: Encoder[Car] = deriveEncoder
}

object Status {
  implicit val jsonDecoder: Decoder[Status] = deriveDecoder
  implicit val jsonEncoder: Encoder[Status] = deriveEncoder
}
