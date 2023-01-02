package com.wayne.routes

import com.wayne.routes.testEndpoints.HelloApp
import io.circe.syntax.EncoderOps
import sttp.apispec.openapi.OpenAPI
import sttp.apispec.openapi.circe.encoderOpenAPI
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.json.circe.{jsonBody, schemaForCirceJson}
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.ztapir.{RichZEndpoint, ZServerEndpoint, endpoint, stringBody}
import zhttp.http.HttpApp
import zio.ZIO

object Endpoints extends HelloApp {
  private[this] val allEndpoints = helloRoutes.map(_.endpoint)
  private[this] val docs: OpenAPI =
    OpenAPIDocsInterpreter().toOpenAPI(
      allEndpoints,
      "ZIO Project API",
      "1.0.0"
    )

  // GET /docs - Serves OPEN-API documentation JSON
  private[this] val docsServerEndpoint: ZServerEndpoint[Any, Any] =
    endpoint.get
      .in("docs")
      .out(jsonBody[io.circe.Json])
      .errorOut(stringBody).zServerLogic(_ => ZIO.succeed(docs.asJson))

  private[this] val docsServer: HttpApp[Any, Throwable] =
    ZioHttpInterpreter().toHttp(docsServerEndpoint)
  private[this] val coreAppServer = (helloServer ++ docsServer)
  val app = coreAppServer
}
