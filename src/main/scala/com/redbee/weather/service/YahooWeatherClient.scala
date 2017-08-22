package com.redbee.weather.service

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.redbee.weather.Config._
import com.redbee.weather._
import com.redbee.weather.controller.BoardJsonProtocol
import com.typesafe.scalalogging.LazyLogging
import spray.can.Http
import spray.http.{HttpRequest, HttpResponse, StatusCodes, Uri}
import spray.httpx.RequestBuilding._

import scala.concurrent.Future
import scala.concurrent.duration._
import spray.json._

object YahooWeatherClient extends BoardJsonProtocol
  with LazyLogging{

  implicit val actorSystem = ActorSystem()
  implicit val ec = actorSystem.dispatcher

  private def resolveRequest(request: HttpRequest): Future[HttpResponse] = {
    implicit val timeout: Timeout = Timeout(10.seconds)
    (IO(Http) ? request).mapTo[HttpResponse]
  }

  def getWoeidFor(location: String): Future[Option[MainBody[WoeidResponse]]] = {
    val query = woeidSelect + location + "'"
    val url = Uri(yahooApiUrl).withQuery("q" -> query, "format" -> "json")

    val request = Get(url)

    resolveRequest(request).flatMap{ response =>
      response.status match {
        case StatusCodes.OK => {
          val jsonAst = response.entity.asString.parseJson
          Future(Some(jsonAst.convertTo[MainBody[WoeidResponse]]))
        }
        case other => {
          logger.error(s"Error while obtain Woeid for location: $location", other)
          Future(None)
        }
      }
    }
  }

  def getForecastFor(woeid: String): Future[Option[MainBody[ResultResponse]]] = {
    val query = forecastSelect + woeid + "'"
    val url = Uri(yahooApiUrl).withQuery("q"->query, "format" -> "json")

    val request = Get(url)

    resolveRequest(request).flatMap{ response =>
      response.status match {
        case StatusCodes.OK => {
          val jsonAst = response.entity.asString.parseJson
          Future(Some(jsonAst.convertTo[MainBody[ResultResponse]]))
        }
        case other => {
          logger.error(s"Error while obtain Forecast for woeid: $woeid", other)
          Future(None)
        }
      }
    }
  }

}
