package com.fedosique.carsharing.logic

import com.fedosique.carsharing.{Car, User}

import java.util.UUID

trait AdminService[F[_]] {

  def getCar(id: UUID): F[Option[Car]]

  def addCar(car: Car): F[UUID]

  def cars: F[Seq[Car]]

  def addUser(name: String, email: String): F[User]

  def getUser(id: UUID): F[Option[User]]
}
