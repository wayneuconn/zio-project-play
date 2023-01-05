package com.wayne.utils.config

import com.wayne.services.BaseAccessible
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderFailures
import pureconfig.generic.auto.exportReader
import zio.{TaskLayer, ZIO, ZLayer}

final case class ApplicationConf(
  prod: DatabasesConf
)

final case class DatabaseSourceConf(
  driver: String,
  url: String,
  username: String,
  password: String,
  connectionPoolSettings: ConnectionPoolSettingsConf = ConnectionPoolSettingsConf.default
)

final case class DatabasesConf(
  user: DatabaseSourceConf
)

final case class ConnectionPoolSettingsConf(
  connectionTimeout: Long,
  keepAliveTime: Long,
  maxLifetime: Long,
  poolSize: Int
)

object ConnectionPoolSettingsConf {
  val default: ConnectionPoolSettingsConf = ConnectionPoolSettingsConf(
    connectionTimeout = 30000,
    keepAliveTime = 120000,
    maxLifetime = 1800000,
    poolSize = 6
  )
}

final case class ApplicationConfReadFailure(failures: ConfigReaderFailures) extends Throwable(failures.prettyPrint())

object ApplicationConf extends BaseAccessible[ApplicationConf] {
  private[this] val liveAppConf =
    ZIO.fromEither(ConfigSource.default.load[ApplicationConf])
      .tapErrorCause(ZIO.logErrorCause(s"Failure parsing app-config", _))

  val live: TaskLayer[ApplicationConf] = ZLayer.fromZIO(liveAppConf)
    .mapError(ApplicationConfReadFailure.apply)
}