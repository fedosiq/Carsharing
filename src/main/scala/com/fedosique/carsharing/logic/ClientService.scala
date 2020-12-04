package com.fedosique.carsharing.logic

import java.util.UUID

import com.fedosique.carsharing.{Car, Location}


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
}
