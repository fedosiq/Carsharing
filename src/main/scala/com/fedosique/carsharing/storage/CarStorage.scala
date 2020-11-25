package com.fedosique.carsharing.storage

import com.fedosique.carsharing.Car


trait CarStorage[F[_]] {

  def put(id: Int, car: Car): F[Unit]

  def get(id: Int): F[Option[Car]]

  def listAll(): F[Seq[Car]]

  def contains(id: Int): F[Boolean]
}
