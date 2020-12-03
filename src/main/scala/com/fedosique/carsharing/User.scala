package com.fedosique.carsharing

import java.util.UUID

import io.circe._
import io.circe.generic.semiauto._


final case class User(id: UUID, name: String)

object User {
  implicit val jsonDecoder: Decoder[User] = deriveDecoder
  implicit val jsonEncoder: Encoder[User] = deriveEncoder
}
