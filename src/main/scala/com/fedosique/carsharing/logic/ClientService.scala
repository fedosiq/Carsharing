package com.fedosique.carsharing.logic

import java.util.UUID

import com.fedosique.carsharing.{Car, Location}


trait ClientService[F[_]] {

  def getCar(id: UUID): F[Option[Car]]

  def freeCars(loc: Location): F[Seq[Car]]
}
