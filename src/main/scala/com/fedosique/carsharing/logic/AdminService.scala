package com.fedosique.carsharing.logic

import java.util.UUID
import com.fedosique.carsharing.{Car, User}

trait AdminService[F[_]] {

  def getCar(id: UUID): F[Option[Car]]

  def addCar(car: Car): F[UUID]

  def cars: F[Seq[Car]]

  def addUser(name: String, email: String): F[User]

  def getUser(id: UUID): F[Option[User]]
}
