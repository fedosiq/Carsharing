package com.fedosique.carsharing.logic

import cats._
import com.fedosique.carsharing.storage.{CarStorage, UserStorage}
import com.fedosique.carsharing.models.{Car, User}

import java.util.UUID

class AdminServiceGenericImpl[F[_] : Monad, DbEffect[_] : Monad](carStorage: CarStorage[DbEffect], userStorage: UserStorage[DbEffect])
                                                                (implicit evalDb: DbEffect ~> F) extends AdminService[F] {

  override def getCar(id: UUID): F[Option[Car]] = evalDb(carStorage.get(id))

  override def addCar(car: Car): F[UUID] = evalDb(carStorage.put(car))

  override def updateCar(car: Car): F[Car] = evalDb(carStorage.update(car.id, car))

  override def cars: F[Seq[Car]] = evalDb(carStorage.listAll())

  override def addUser(name: String, email: String): F[User] = evalDb(userStorage.put(User(UUID.randomUUID(), name, email)))

  override def getUser(id: UUID): F[Option[User]] = evalDb(userStorage.get(id))
}
