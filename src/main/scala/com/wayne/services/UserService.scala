package com.wayne.services

import com.wayne.daos.UserDao
import com.wayne.daos.models.User
import zio.{Task, ZLayer}

sealed trait UserService {
  def getUserByEmail(email: String): Task[Option[User]]
}

final case class UserServiceLive private(userDao: UserDao) extends UserService {
  override def getUserByEmail(email: String): Task[Option[User]] = userDao.getUserByEmail(email)
}

object UserService extends BaseAccessible[UserService] {
  val live = ZLayer.fromFunction(UserServiceLive.apply _)
}