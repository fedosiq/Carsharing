package com.fedosique.carsharing.storage

import com.fedosique.carsharing.User
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, Rep, Tag}

import scala.concurrent.ExecutionContext
import java.util.UUID

class SlickUserStorage(implicit ec: ExecutionContext) extends UserStorage[DBIO] {

  class UsersTable(tag: Tag) extends Table[User](tag, "users") {
    def id: Rep[UUID] = column("id", O.PrimaryKey)

    def name: Rep[String] = column("name")

    def email: Rep[String] = column("email")

    def isRenting: Rep[Boolean] = column("is_renting")


    override def * : ProvenShape[User] =
      (id, name, email, isRenting) <> ((User.apply _).tupled, User.unapply)
  }

  private val AllUsers = TableQuery[UsersTable]

  def put(user: User): DBIO[User] = (AllUsers += user).map(_ => user)

  def update(id: UUID, user: User): DBIO[User] =
    AllUsers
      .filter(_.id === id)
      .update(user)
      .map(_ => user)

  def get(id: UUID): DBIO[Option[User]] =
    AllUsers
      .filter(_.id === id)
      .result
      .headOption

  def contains(id: UUID): DBIO[Boolean] = get(id).map(_.isDefined)
}
