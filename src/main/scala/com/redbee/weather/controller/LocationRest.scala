package com.redbee.weather.controller

import com.redbee.weather.{Location, MainActor}
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

  private def save =
    post {
      path(boardPath / Segment){ name =>
        entity(as[Location]){ newLocation =>
          onComplete(LocationService.save(newLocation, name)){
            case Success(newLocation) => complete(StatusCodes.Created, newLocation)
            case Failure(error) => complete(StatusCodes.InternalServerError, error)
          }
        }
      }
    }

  val locationRoute: Route = save
}
