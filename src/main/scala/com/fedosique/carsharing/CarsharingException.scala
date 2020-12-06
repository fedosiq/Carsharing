package com.fedosique.carsharing

import java.util.UUID

sealed abstract class CarsharingException(message: String) extends Exception(message)

final case class CarAlreadyOccupiedException(carId: UUID) extends CarsharingException(s"Car with id=$carId is already occupied")

final case class UserNotFoundException(userId: UUID) extends CarsharingException(s"User with id=$userId not found")

final case class UserAlreadyStartedRenting(userId: UUID) extends CarsharingException(s"User with id=$userId is already renting a car")
