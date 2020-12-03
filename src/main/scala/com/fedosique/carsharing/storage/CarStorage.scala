package com.fedosique.carsharing.storage

import java.util.UUID

import com.fedosique.carsharing.Car


trait CarStorage[F[_]] {

  def put(id: UUID, car: Car): F[Unit]

  def get(id: UUID): F[Option[Car]]

  def listAll(): F[Seq[Car]]

  def contains(id: UUID): F[Boolean]
}
