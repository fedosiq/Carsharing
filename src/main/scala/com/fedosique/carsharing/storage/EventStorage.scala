package com.fedosique.carsharing.storage

import com.fedosique.carsharing.model.Event

import java.time.Instant
import java.util.UUID

trait EventStorage[F[_]] {

  def put(event: Event): F[Event]

  def update(id: UUID, event: Event): F[Event]

  def get(id: UUID): F[Option[Event]]

  def listAll(): F[Seq[Event]]

  def getLastOccupationTime(userId: UUID): F[Option[Instant]]
}
