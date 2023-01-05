package com.wayne.routes.testEndpoints

import sttp.tapir.Endpoint
import sttp.tapir.ztapir._
import com.wayne.services.UserService
import sttp.tapir.server.ziohttp.ZioHttpInterpreter

trait UserRoutes {
  //POST /user/{email}
  private[this] def checkUserEmailDef: Endpoint[Unit, String, String, String, Any] =
    endpoint.post
      .in("user")
      .in(path[String]("email"))
      .out(stringBody)
      .errorOut(stringBody)

  private[this] def checkUserEmailEndpoint: ZServerEndpoint[UserService, Any] =
    checkUserEmailDef.zServerLogic(implicit email => {
      UserService(_.getUserByEmail(email))
        .mapAttempt(maybeUser => maybeUser.map(_.name).getOrElse("User Not found"))
        .mapError(_.getMessage)
    })

  val userRoutes = List(
    checkUserEmailEndpoint
  )
  val userServer = ZioHttpInterpreter().toHttp(userRoutes)
}
