package com.fedosique.carsharing.logic

import cats._
import com.fedosique.carsharing.storage.{CarStorage, UserStorage}
import com.fedosique.carsharing.{Car, User}

import java.util.UUID

class AdminServiceGenericImpl[F[_]: Monad, DbEffect[_]: Monad](carStorage: CarStorage[DbEffect], userStorage: UserStorage[DbEffect])
                                                                (implicit evalDb: DbEffect ~> F) extends AdminService[F] {

  def getCar(id: UUID): F[Option[Car]] = evalDb(carStorage.get(id))

  def addCar(car: Car): F[UUID] = evalDb(carStorage.put(car))

  def cars: F[Seq[Car]] = evalDb(carStorage.listAll())

  def addUser(name: String, email: String): F[User] = evalDb(userStorage.put(User(UUID.randomUUID(), name, email)))

  def getUser(id: UUID): F[Option[User]] = evalDb(userStorage.get(id))
}
