package com.fedosique.carsharing

import io.circe._
import io.circe.generic.semiauto._

import java.util.UUID


final case class User(id: UUID, name: String, email: String, isRenting: Boolean)

object User {
  implicit val jsonDecoder: Decoder[User] = deriveDecoder
  implicit val jsonEncoder: Encoder[User] = deriveEncoder
}
