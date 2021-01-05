package com.fedosique.carsharing.storage

import com.fedosique.carsharing.model.{Event, Location}
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, Rep, Tag}

import java.time.Instant
import java.util.UUID
import scala.concurrent.ExecutionContext


class SlickEventStorage(implicit ec: ExecutionContext) extends EventStorage[DBIO] {

  class EventsTable(tag: Tag) extends Table[Event](tag, "events") {

    def id: Rep[UUID] = column("id", O.PrimaryKey)

    def event: Rep[String] = column("event")

    def carId: Rep[UUID] = column("car_id")

    def userId: Rep[UUID] = column("user_id")

    def lat: Rep[Double] = column("lat")

    def lon: Rep[Double] = column("lon")

    def timestamp: Rep[Instant] = column("timestamp")


    override def * : ProvenShape[Event] =
      (id, event, carId, userId, (lat, lon), timestamp) <>
        ( {
          case (id, event, carId, userId, location, timestamp) =>
            Event(id, event, carId, userId, (Location.apply _).tupled(location), timestamp)
        }, { e: Event =>
          Some((e.id, e.event, e.carId, e.userId, Location.unapply(e.location).get, e.timestamp))
        })
  }

  val AllEvents = TableQuery[EventsTable]

  def put(event: Event): DBIO[Event] = (AllEvents += event).map(_ => event)

  def update(id: UUID, event: Event): DBIO[Event] =
    AllEvents
      .filter(_.id === id)
      .update(event)
      .map(_ => event)

  def get(id: UUID): DBIO[Option[Event]] =
    AllEvents
      .filter(_.id === id)
      .result
      .headOption

  def listAll(): DBIO[Seq[Event]] = AllEvents.result

  def getLastOccupationTime(userId: UUID): DBIO[Option[Instant]] =
    AllEvents
      .filter(_.userId === userId)
      .sortBy(_.timestamp.desc)
      .map(_.timestamp)
      .result
      .headOption
}
