package com.redbee.weather.service

import java.sql.Timestamp

import com.redbee.weather.Config.db
import com.redbee.weather._
import com.redbee.weather.Tables.newsTable
import org.joda.time.DateTime
import slick.driver.MySQLDriver.api._

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

object NewsService {

  def saveNewsFrom(forecasts: Option[MainBody[ResultResponse]], woeid: String): Future[Seq[Forecast]] = forecasts match {
    case None => Future.failed(ForecastNotFound())
    case Some(body) => {
      val condition = body.query.results.channel.item.condition
      val forecasts = body.query.results.channel.item.forecast
      val newId = for {
        id <- save(
          News(None,woeid, new Timestamp(DateTime.now.getMillis), condition.date, condition.temp, condition.text))
      } yield id

      newId.flatMap{ id =>
        val forecastsList = forecasts.map{ item =>
          Forecast(None, id, woeid, item.day + " " + item.date, item.high.toInt, item.low.toInt, item.text)
        }
        ForecastService.save(forecastsList)
      }
    }
  }

  def save(news: News): Future[Long] = {
    db.run(newsTable returning newsTable += news).map(_.id.get)
  }
}
