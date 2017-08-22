package com.redbee.weather.service

import com.redbee.weather._
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future
import com.redbee.weather.Config._
import Tables._
import slick.driver.PostgresDriver.api._

object ForecastService extends LazyLogging{

  def save(forecast: Seq[Forecast]): Future[Seq[Forecast]] = {
    db.run(forecastTable returning forecastTable ++= forecast)
  }
}
