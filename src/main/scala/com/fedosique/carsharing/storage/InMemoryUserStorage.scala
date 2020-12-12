package com.fedosique.carsharing.storage

import com.fedosique.carsharing.models.User
import monix.eval.Task

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class InMemoryUserStorage extends UserStorage[Task] {
  override def put(user: User): Task[User] = Task(storage.put(user.id, user)).map(_ => user)

  override def update(id: UUID, user: User): Task[User] = Task(storage.replace(id, user)).map(_ => user)

  override def get(id: UUID): Task[Option[User]] = Task {
    if (storage.containsKey(id)) Some(storage.get(id))
    else None
  }

  override def contains(id: UUID): Task[Boolean] = Task(storage.containsKey(id))

  private val storage = new ConcurrentHashMap[UUID, User]
}
