package com.redbee.weather.service

import com.redbee.weather._
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.redbee.weather.Config._
import Tables._
import slick.driver.PostgresDriver.api._

object ForecastService extends LazyLogging{

  def saveNewsFrom(forecasts: Option[MainBody[ResultResponse]], woeid: String): Future[Seq[Forecast]] = forecasts match {
    case None => Future.failed(ForecastNotFound())
    case Some(body) => {
      val condition = body.query.results.channel.item.condition
      val forecasts = body.query.results.channel.item.forecast
      val newId = for {
        id <- saveNew(News(None,woeid, condition.date, condition.temp, condition.text))
      } yield id

      newId.flatMap{ id =>
        val forecastsList = forecasts.map{ item =>
          Forecast(None, id, woeid, item.day + " " + item.date, item.high.toInt, item.low.toInt, item.text)
        }
        save(forecastsList)
      }
    }
  }

  def save(forecast: Seq[Forecast]): Future[Seq[Forecast]] = {
    db.run(forecastTable returning forecastTable ++= forecast)
  }

  def saveNew(news: News): Future[Long] = {
    db.run(newsTable returning newsTable += news).map(_.id.get)
  }

  def getByWithNews(woeid: String): Future[Option[LocationWithNewsAndForecasts]] = {
    val query = for {
      location <- locationTable.filter(_.woeid === woeid).result.head
      news <- newsTable.filter(n => n.woeid === woeid).sortBy(_.id.desc).result.head
      forecasts <- forecastTable.filter( f => f.newsId === news.id).result
    } yield (location, news, forecasts)

    db.run(query).map{ tuple =>
      Some(LocationWithNewsAndForecasts(tuple._1, tuple._2, tuple._3))
    }
  }
}
