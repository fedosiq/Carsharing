package com.fedosique.carsharing.storage

import com.fedosique.carsharing.model.{Car, Location, Status}
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, Rep, TableQuery, Tag}

import java.time.Instant
import scala.concurrent.ExecutionContext
import java.util.UUID


class SlickCarStorage(implicit ec: ExecutionContext) extends CarStorage[DBIO] {

  class CarsTable(tag: Tag) extends Table[Car](tag, "cars") {
    def id: Rep[UUID] = column("id", O.PrimaryKey)

    def name: Rep[String] = column("name")

    def color: Rep[String] = column("color")

    def plateNumber: Rep[String] = column("plate_number")

    def lat: Rep[Double] = column("lat")

    def lon: Rep[Double] = column("lon")

    def fuel: Rep[Double] = column("fuel")

    def occupiedBy: Rep[UUID] = column("occupied_by")

    def lastUpdate: Rep[Instant] = column("last_update")

    def price: Rep[Double] = column("price")


    override def * : ProvenShape[Car] =
      (id, name, color, plateNumber, (lat, lon), (fuel, occupiedBy.?, lastUpdate), price) <>
        ( {
          case (id, name, color, plateNumber, location, status, price) =>
            Car(id, name, color, plateNumber, (Location.apply _).tupled(location), (Status.apply _).tupled(status), price)
        }, { c: Car =>
          Some((c.id, c.name, c.color, c.plateNumber, Location.unapply(c.location).get, Status.unapply(c.status).get, c.price))
        })
  }

  private val AllCars = TableQuery[CarsTable]

  def put(car: Car): DBIO[UUID] = (AllCars += car).map(_ => car.id)

  def update(id: UUID, car: Car): DBIO[Car] =
    AllCars
      .filter(_.id === id)
      .update(car)
      .map(_ => car)

  def get(id: UUID): DBIO[Option[Car]] =
    AllCars
      .filter(_.id === id)
      .result
      .headOption

  def listAll(): DBIO[Seq[Car]] = AllCars.result

  def contains(id: UUID): DBIO[Boolean] = get(id).map(_.isDefined)
}
