package com.redbee.weather.service

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import com.redbee.weather.{Config, RequestLimit, YahooRequestLimitExceeded}

import scala.concurrent.Future
import com.redbee.weather.Tables._
import slick.driver.PostgresDriver.api._
import com.redbee.weather.Config._
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
object RequestLimitService extends LazyLogging{

  def verify(): Future[Boolean] = {
    findPetitions().flatMap{
      case Some(rl) => verifyPetitionsLimit(rl)
      case None => addPetitionLimit
    }
  }

  private def addPetitionLimit: Future[Boolean] = {
    val currentDate = LocalDate.now
    db.run(requestLimitTable returning requestLimitTable += RequestLimit(Timestamp.valueOf(currentDate.atStartOfDay()), 1)).map(_.quantity > 0)
  }

  private def findPetitions(): Future[Option[RequestLimit]] ={
    db.run(requestLimitTable.result.headOption)
  }

  private def updatePetitionLimit(limit: RequestLimit): Future[Boolean] = {
    db.run(requestLimitTable.update(limit)).map(_ > 0)
  }

  private def verifyPetitionsLimit(rl: RequestLimit): Future[Boolean] = {
    val currentDate = LocalDate.now

    currentDate.isAfter(rl.date.toLocalDateTime.toLocalDate) match  {
      case true => updatePetitionLimit(rl.copy(date = Timestamp.valueOf(currentDate.atStartOfDay())))
      case false =>
        if (rl.quantity >= Config.petitionLimit) {
          logger.error("Yahoo client: Petition limit exceeded.",YahooRequestLimitExceeded())
          Future(false)
        }
        else updatePetitionLimit(rl.copy(quantity = rl.quantity + 1))
    }
  }

}
