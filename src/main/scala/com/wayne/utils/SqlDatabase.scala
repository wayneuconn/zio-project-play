package com.wayne.utils

import com.wayne.utils.config.{ApplicationConf, DatabaseSourceConf}
import com.wayne.utils.sql.DatabaseUtils
import scalikejdbc.{DBConnection, NamedDB}
import zio._

sealed trait SqlDatabase {
  def dbName: String

  def apply[A](execution: DBConnection => A): Task[A] =
    ZIO.attemptBlocking(NamedDB(dbName)).mapAttempt(execution)

  def close(): URIO[ApplicationConf, Unit] = {
    DatabaseUtils.closeDB(dbName)
  }
}

object SqlDatabase {
  def namedDB[DB <: SqlDatabase](
    dbName: String,
    getConnectionPoolSetting: ApplicationConf => DatabaseSourceConf,
    makeConnectionLayer: String => DB,
    autoClose: Boolean = true
  )(implicit tag: Tag[DB]): RLayer[ApplicationConf, DB] =
    ZLayer.scoped(
      ZIO.acquireRelease(
        acquire = DatabaseUtils
          .setupDB(dbName, getConnectionPoolSetting)
          .mapAttempt(_ => makeConnectionLayer(dbName))
      )(
        release = conn => if (autoClose) conn.close() else ZIO.succeed({})
      )
    )
}

final case class UsersDb(dbName: String) extends SqlDatabase

object UsersDb {
  val live = SqlDatabase.namedDB(DatabaseUtils.user, _.prod.user, UsersDb(_))
}

