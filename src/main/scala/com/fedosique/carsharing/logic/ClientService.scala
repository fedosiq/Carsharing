package com.fedosique.carsharing.logic

import com.fedosique.carsharing.Car


trait ClientService[F[_]] {

  def addCar(car: Car): F[Int]

  def getCar(id: Int): F[Option[Car]]

  def carList: F[Seq[Car]]
}
