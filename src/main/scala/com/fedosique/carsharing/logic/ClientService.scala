package com.fedosique.carsharing.logic

import com.fedosique.carsharing.models.{Car, Location}

import java.util.UUID


trait ClientService[F[_]] {

  /**
   * Shows info about car with given id if it's not occupied
   *
   * @param id car id
   * @return car if it's not occupied
   * */
  def getCar(id: UUID): F[Option[Car]]

  /**
   * Shows available cars nearby
   *
   * @param loc user's location
   * @return available cars sorted by distance from user's location
   * */
  def availableCars(loc: Location): F[Seq[Car]]

  /**
   * Occupies car with given user
   *
   * @param carId  id of a car to be occupied
   * @param userId occupier id
   * @return occupied car info
   * @throws CarNotFoundException        if carId is not in db
   * @throws UserNotFoundException       if userId is not in db
   * @throws UserAlreadyRentingException if user tries to rent more than one car
   * */
  def occupyCar(carId: UUID, userId: UUID): F[Car]

  /**
   * Makes car available if it's occupied by given user
   *
   * @param carId  id of a car to be left
   * @param userId occupier id
   * @return left car info
   * @throws CarNotFoundException  if carId is not in db
   * @throws UserNotFoundException if userId is not in db
   * @throws CarOccupiedByOtherUser if user tries to leave other user's car
   * @throws CarNotOccupiedException if user tries to leave car that he didn't occupy
   * */
  def leaveCar(carId: UUID, userId: UUID): F[Car]
}
