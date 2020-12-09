package com.fedosique.carsharing.storage

import com.fedosique.carsharing.Car

import java.util.UUID


trait CarStorage[F[_]] {

  def put(car: Car): F[UUID]

  def update(id: UUID, car: Car): F[Car]

  def get(id: UUID): F[Option[Car]]

  def listAll(): F[Seq[Car]]

  def contains(id: UUID): F[Boolean]
}
