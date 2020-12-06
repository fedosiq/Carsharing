package com.fedosique.carsharing.storage

import com.fedosique.carsharing.User

import java.util.UUID

trait UserStorage[F[_]] {
  def put(user: User): F[Unit]

  def get(id: UUID): F[Option[User]]

  def contains(id: UUID): F[Boolean]
}
