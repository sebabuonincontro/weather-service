package com.redbee.weather.controller

import com.redbee.weather.actor.MainActor
import spray.http.HttpHeaders.{`Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin`}
import spray.http.HttpMethods.OPTIONS
import spray.http.{AllOrigins, HttpMethod, HttpMethods, HttpResponse}
import spray.routing.{Directive0, MethodRejection, Rejected, RequestContext}

trait CORSSupport {
  this: MainActor =>

  def isOptionsMethod(ctx: RequestContext): Boolean = {
    ctx.request.method.equals(HttpMethods.OPTIONS)
  }

  def cors[T]: Directive0 = mapRequestContext {
    ctx => ctx.withRouteResponseHandling({
      //It is an option request for a resource that responds to some other method
      case Rejected(x) if isOptionsMethod(ctx) =>
        val allowedMethods: List[HttpMethod] = x.filter(_.isInstanceOf[MethodRejection]).map(
          rejection => rejection.asInstanceOf[MethodRejection].supported
        )
        ctx.complete(
          HttpResponse().withHeaders(
            List(`Access-Control-Allow-Methods`(OPTIONS, allowedMethods: _*))
          )
        )
    })
  }
}
