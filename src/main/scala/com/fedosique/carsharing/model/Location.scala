package com.fedosique.carsharing.model

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class Location(lat: Double, lon: Double)

object Location {
  implicit val jsonDecoder: Decoder[Location] = deriveDecoder
  implicit val jsonEncoder: Encoder[Location] = deriveEncoder
}
