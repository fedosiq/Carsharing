package com.fedosique.carsharing.storage

import com.fedosique.carsharing.User
import monix.eval.Task

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class InMemoryUserStorage extends UserStorage[Task] {
  override def put(user: User): Task[Unit] = Task(storage.put(user.id, user))

  override def get(id: UUID): Task[Option[User]] = Task {
    if (storage.containsKey(id)) Some(storage.get(id))
    else None
  }

  override def contains(id: UUID): Task[Boolean] = Task(storage.containsKey(id))

  private val storage = new ConcurrentHashMap[UUID, User]
}
