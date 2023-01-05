package com.wayne.services

import com.wayne.utils.config.ApplicationConf
import zio.ZLayer

sealed trait AuthError {
  self: Throwable =>

}

final case class AuthenticationFailure(idToken: String)
  extends Throwable(s"failed to authenticate token: ${idToken}")
    with AuthError

final case class AuthorizationFailure(email: String)
  extends Throwable(s"failed to authorize user: ${email}")
    with AuthError

trait AuthService {
}

final case class AuthServiceLive private(
  userService: UserService,
  applicationConf: ApplicationConf
) extends AuthService {
  private[this] def isAdminUser(email: String) = {
    userService.getUserByEmail(email).collect(
      AuthorizationFailure(email)
    ) {
      case Some(user) if user.isAdmin => email
    }
  }
}

object AuthService {
  val live = ZLayer.fromFunction(AuthServiceLive.apply _)
}