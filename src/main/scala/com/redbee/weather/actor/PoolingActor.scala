package com.redbee.weather.actor

import java.time.Instant

import akka.actor.{Actor, ActorLogging}
import com.redbee.weather.LocationNotFound
import com.redbee.weather.service.{ForecastService, LocationService, YahooWeatherClient}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class PoolingActor extends Actor
  with ActorLogging {

  override def receive = {
    case GetNews => getNews
  }

  private def getNews() = {
    //TODO this is a sample.
    log.info("Get Locations News at: " + Instant.now().toString)

    val locations = for {
      result <- LocationService.getAll()
    } yield result

    locations.flatMap( x => Future(x.map { location =>
        for {
          response <- YahooWeatherClient.getForecastFor(location.woeid)
          news <- ForecastService.saveNewsFrom(response, location.woeid)
        } yield news

    }))
  }

}

case class GetNews()