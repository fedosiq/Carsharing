package com.fedosique.carsharing

import io.circe._
import io.circe.generic.semiauto._


final case class Car(
                //                id: Int,
                name: String,
                color: String,
                plateNumber: String,
                lat: Double, // or BigDecimal
                lon: Double,
                fuel: Double,
                isOccupied: Boolean)

object Car {
  implicit val jsonDecoder: Decoder[Car] = deriveDecoder
  implicit val jsonEncoder: Encoder[Car] = deriveEncoder
}
