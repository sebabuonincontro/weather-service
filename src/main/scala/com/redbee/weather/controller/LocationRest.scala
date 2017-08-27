package com.redbee.weather.controller

import akka.actor.{ActorSystem, Props}
import com.redbee.weather.actor.messages.{GetNews, GetNewsFor}
import com.redbee.weather.{BoardNotFound, Location}
import com.redbee.weather.actor.{MainActor, PoolingActor}
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

  val actorSystem = ActorSystem()
  val poolingActor = actorSystem.actorOf(Props(new PoolingActor), "pooling-actor")
  val locationPath = "location"

  private def save =
    post {
      path(boardPath / Segment){ board =>
        entity(as[Location]){ location =>
          onComplete( LocationService save(location, board) ){
            case Success(newLocation) => {
              poolingActor ! GetNewsFor(newLocation)
              complete(StatusCodes.Created, newLocation)
            }
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
        onComplete( LocationService.getWithNewsBy(woeid)){
          case Success(Some(found)) => complete(StatusCodes.OK, found)
          case Success(None) => complete(StatusCodes.NotFound)
          case Failure(error) => complete(StatusCodes.InternalServerError, error)
        }
      }
    }

  private def remove =
    delete {
      path(boardPath / Segment / Segment){ (board, woeid) =>
        onComplete( LocationService.remove(board, woeid)){
          case Success(_) => complete(StatusCodes.OK)
          case Failure(error) => complete(StatusCodes.InternalServerError, error)
        }
      }
    }

  val locationRoute: Route = save ~ getBy ~ remove
}
