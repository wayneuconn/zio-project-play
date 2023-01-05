package com.wayne.utils

import sttp.tapir.model.ServerRequest

object EndpointUtils {
  type ServerRequestWithParam[T] = (T, ServerRequest)
}