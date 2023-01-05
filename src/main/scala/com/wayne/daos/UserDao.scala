package com.wayne.daos

import com.wayne.daos.models.User
import com.wayne.utils.UsersDb
import scalikejdbc.scalikejdbcSQLInterpolationImplicitDef
import zio.{RLayer, Task, ZIO, ZLayer}

trait UserDao {
  def getUserByEmail(email: String): Task[Option[User]]
}

final case class UserDaoLive(usersDb: UsersDb) extends UserDao {
  override def getUserByEmail(email: String): Task[Option[User]] = usersDb(_.readOnly { implicit session =>
    val c = User.column
    val result =
      sql"""select ${c.id}, ${c.name}, ${c.email}, ${c.access}, ${c.apiKey} FROM ${User.table} WHERE ${c.email} = $email"""
        .map(User.parseSql).headOption.apply()
    result
  }).tap(user => ZIO.logInfo(s"fetched user: ${user.map(_.email)}"))
}

object UserDao {
  val live: RLayer[UsersDb, UserDao] = ZLayer.fromFunction(UserDaoLive.apply _)
}

