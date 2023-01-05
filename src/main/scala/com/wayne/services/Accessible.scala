package com.wayne.services

import com.wayne.utils.EndpointUtils
import zhttp.http.Request
import zio.{Tag, ZIO}

trait ServerEndpointAccessible[Service] {
  def apply[R <: Service, E, A](f: Service => ZIO[R, E, A])(implicit tag: Tag[Service], req: EndpointUtils.ServerRequestWithParam[_]) = {
    ZIO.serviceWithZIO[Service](service => f(service))
  }
}

trait BaseAccessible[Service] {
  def apply[R <: Service, E, A](f: Service => ZIO[R, E, A])(implicit tag: Tag[Service]) =
    ZIO.serviceWithZIO[Service](f)
}

trait MiddlewareAccessible[Service] {
  def apply[R <: Service, E, A](f: Service => ZIO[R, E, A], req: Request)(implicit tag: Tag[Service]) =
    ZIO.serviceWithZIO[Service](service => f(service))
}
