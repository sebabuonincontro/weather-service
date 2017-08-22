package com.redbee.weather.actor

import java.time.Instant

import akka.actor.{Actor, ActorLogging}
import com.redbee.weather.Location
import com.redbee.weather.actor.messages.{GetNews, GetNewsFor}
import com.redbee.weather.service.{ForecastService, LocationService, NewsService, YahooWeatherClient}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class PoolingActor extends Actor
  with ActorLogging {

  override def receive = {
    case GetNews => getNews
    case GetNewsFor(location) => getNewsFor(location)
  }

  private def getNews() = {
    log.info("Get Locations News at: " + Instant.now().toString)

    LocationService.getAll().onComplete{
      case Success(locations) => locations map getNewsFor
      case Failure(error) => log.error("PoolingActor - Error while obtain locations :", error)
    }
  }

  private def getNewsFor(location: Location) = {
    for {
      response <- YahooWeatherClient.getForecastFor(location.woeid.get)
      news <- NewsService.saveNewsFrom(response, location.woeid.get)
    } yield news
  }
}

object messages {
  case class GetNews()
  case class GetNewsFor(location: Location)
}
