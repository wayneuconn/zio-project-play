package com.wayne.routes.testEndpoints

import sttp.tapir.{endpoint, path, stringBody}
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.ztapir.{RichZEndpoint, ZServerEndpoint}
import zio.ZIO

trait HelloApp {
  // GET /hello/{name}
  private[this] val helloNameEndpointImpl: ZServerEndpoint[Any, Any] =
    endpoint.get
      .in("hello")
      .in(path[String]("name"))
      .out(stringBody)
      .zServerLogic(
        name => ZIO.succeed(s"Hello, $name!")
      )

  // GET /hello
  private[this] val helloWorldEndpointImpl: ZServerEndpoint[Any, Any] =
    endpoint.get
      .in("hello")
      .out(stringBody)
      .zServerLogic(
        _ => ZIO.succeed("Hello, World!")
      )

  val helloRoutes = List(
    helloNameEndpointImpl,
    helloWorldEndpointImpl
  )
  val helloServer = ZioHttpInterpreter().toHttp(helloRoutes)
}
