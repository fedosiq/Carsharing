package com.fedosique.carsharing.logic

import java.util.UUID

import com.fedosique.carsharing.Car

trait AdminService[F[_]] {

  def getCar(id: UUID): F[Option[Car]]

  def addCar(car: Car): F[UUID]

  def cars: F[Seq[Car]]
}
