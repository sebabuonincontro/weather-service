package com.redbee.weather.controller

import com.redbee.weather.MainActor
import com.redbee.weather.service.LocationService
import spray.http.StatusCodes
import spray.routing.Route

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by bsbuon on 8/13/17.
  */
trait LocationRest {

  self: MainActor =>

  private val locationsPath = "locations"

  private def getBy =
    get {
      path( boardPath / Segment) { board =>
        onComplete(LocationService.getBy(board)){
          case Success(list) => complete(StatusCodes.OK, list)
          case Failure(error) => complete(StatusCodes.InternalServerError, error)
        }
      }
    }

  val locationRoute: Route = getBy
}
