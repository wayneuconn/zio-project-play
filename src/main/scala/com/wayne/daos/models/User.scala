package com.wayne.daos.models

import scalikejdbc._

final case class InvalidAccessValue(value: String) extends Throwable(
  s"Invalid access value string ${value}. Must be one of ['user', 'site-admin']"
)

sealed trait UserAccessLevel

object UserAccessLevel {
  case object StandardAccess extends UserAccessLevel

  case object SiteAdminAccess extends UserAccessLevel

  def fromString(string: String): UserAccessLevel = string match {
    case "user" => StandardAccess
    case "site-admin" => SiteAdminAccess
    case _ => throw InvalidAccessValue(string)
  }
}

final case class User(
  id: String,
  name: String,
  email: String,
  access: UserAccessLevel,
  apiKey: Option[String]
) {
  val isAdmin: Boolean = access == UserAccessLevel.SiteAdminAccess
}

object User extends SQLSyntaxSupport[User] {
  override val tableName = "user_db"
  override val columns = Seq(
    "id",
    "name",
    "email",
    "access",
    "api_key"
  )

  private[daos] def parseSql(rs: WrappedResultSet): User = User(
    id = rs.string("id"),
    name = rs.string("name"),
    email = rs.string("email"),
    access = UserAccessLevel.fromString(rs.string("access")),
    apiKey = rs.getOpt[String]("api_key")
  )
}
