package com.wayne

import zhttp.service.Server
import zio._
import com.wayne.routes.Endpoints

object ZIOHttpApp extends ZIOAppDefault {
  private[this] val PORT: Int = 9002

  override def run: ZIO[Environment with ZIOAppArgs, Any, Any] = {
    Server.start(
      PORT,
      Endpoints.app
    ).exitCode
  }
}
