package com.wayne

import com.wayne.daos.UserDao
import zhttp.service.Server
import zio._
import com.wayne.routes.Endpoints
import com.wayne.services.UserService
import com.wayne.utils.UsersDb
import com.wayne.utils.config.ApplicationConf

object ZIOHttpApp extends ZIOAppDefault {
  private[this] val PORT: Int = 9002
  type ServiceEnv = UserService

  private[this] def onNonFatalErr(cause: Cause[Any]) = {
    ZIO.logErrorCause("Encountered non-fatal error: ", cause)
  }

  private[this] def provideLayers(serverZio: RIO[ServiceEnv, ExitCode]): RIO[Environment, ExitCode] = {
    serverZio.provide(
      ApplicationConf.live,
      UsersDb.live,
      UserDao.live,
      UserService.live
    )
  }

  override def run: ZIO[Environment with ZIOAppArgs, Any, Any] = {
    val startServer = Server.start(
      PORT,
      Endpoints.app
    ).exitCode

    provideLayers(
      startServer
    ).tapErrorCause(onNonFatalErr)
  }
}
