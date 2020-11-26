package com.fedosique.carsharing.logic

import java.util.UUID

import com.fedosique.carsharing.Car


trait ClientService[F[_]] {

  def addCar(car: Car): F[UUID]

  def getCar(id: UUID): F[Option[Car]]

  def carList: F[Seq[Car]]
}
