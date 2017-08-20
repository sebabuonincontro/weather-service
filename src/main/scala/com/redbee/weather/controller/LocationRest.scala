package com.redbee.weather.controller

import com.redbee.weather.{BoardNotFound, Location}
import com.redbee.weather.actor.MainActor
import com.redbee.weather.service.{ForecastService, LocationService}
import spray.http.StatusCodes
import spray.routing.Route

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by bsbuon on 8/13/17.
  */
trait LocationRest {

  self: MainActor =>

  val locationPath = "location"

  private def save =
    post {
      path(boardPath / Segment){ name =>
        entity(as[Location]){ location =>
          onComplete( LocationService save(location, name) ){
            case Success(newLocation) => complete(StatusCodes.Created, newLocation)
            case Failure(error) => error match {
              case e: BoardNotFound => complete(StatusCodes.InternalServerError, e.getMessage )
              case _ => complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    }

  private def getBy =
    get {
      path(locationPath / Segment){ woeid =>
        onComplete( ForecastService.getByWithNews(woeid)){
          case Success(Some(found)) => complete(StatusCodes.OK, found)
          case Success(None) => complete(StatusCodes.NotFound)
          case Failure(error) => complete(StatusCodes.InternalServerError, error)
        }
      }
    }

  val locationRoute: Route = save ~ getBy
}
