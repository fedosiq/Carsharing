package com.fedosique.carsharing

import io.circe._
import io.circe.generic.semiauto._


final case class User(id: Int, name: String)

object User {
  implicit val jsonDecoder: Decoder[User] = deriveDecoder
  implicit val jsonEncoder: Encoder[User] = deriveEncoder
}
