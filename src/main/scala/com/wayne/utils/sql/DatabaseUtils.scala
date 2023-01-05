package com.wayne.utils.sql

import com.wayne.utils.config.{ApplicationConf, DatabaseSourceConf}
import com.zaxxer.hikari.HikariDataSource
import scalikejdbc.{ConnectionPool, DataSourceConnectionPool}
import scalikejdbc.config.DBs
import zio.{RIO, Task, UIO, URIO, ZIO}

object DatabaseUtils {
  val user = "user_db"

  private[this] def makeDatasource(databaseConf: DatabaseSourceConf) = ZIO.attempt {
    val ds = new HikariDataSource()
    ds.setDriverClassName(databaseConf.driver)
    ds.setJdbcUrl(databaseConf.url)
    ds.setUsername(databaseConf.username)
    ds.setPassword(databaseConf.password)
    val poolSettings = databaseConf.connectionPoolSettings
    // maximum number of milliseconds that a client (that's you) will wait for a connection from the pool
    ds.setConnectionTimeout(poolSettings.connectionTimeout)
    // how frequently HikariCP will attempt to keep a connection alive
    ds.setKeepaliveTime(poolSettings.keepAliveTime)
    // maximum lifetime of a connection in the pool
    ds.setMaxLifetime(poolSettings.maxLifetime)
    // minimum number of idle connections in the pool (same as max pool size)
    ds.setMinimumIdle(poolSettings.poolSize)
    // maximum number of connections in the pool (idle + live)
    ds.setMaximumPoolSize(poolSettings.poolSize)
    new DataSourceConnectionPool(ds)
  }

  private[this] def setupDB(databaseConf: DatabaseSourceConf, name: String): Task[Unit] =
    ZIO.logSpan(s"setup.db.$name") {
      for {
        datasource <- makeDatasource(databaseConf)
        _ = ConnectionPool.add(name, datasource)
        _ <- ZIO.logInfo("Finished DB setup")
      } yield ()
    }

  private[this] def closeDb(applicationConf: ApplicationConf, name: String): UIO[Unit] =
    ZIO.logSpan(s"close.db.$name") {
      for {
        _ <- ZIO.logInfo("Starting closing DB")
        _ <- ZIO.succeed(DBs.close(name))
        _ <- ZIO.logInfo("Finished closing DB")
      } yield ()
    }

  def setupDB(name: String, getConnectionPoolSetting: ApplicationConf => DatabaseSourceConf): RIO[ApplicationConf, Unit] =
    ApplicationConf(conf => setupDB(getConnectionPoolSetting(conf), name))

  def closeDB(name: String): URIO[ApplicationConf, Unit] = ApplicationConf(conf => closeDb(conf, name))
}
