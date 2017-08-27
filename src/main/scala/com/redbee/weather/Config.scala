package com.redbee.weather

import com.typesafe.config.ConfigFactory
import slick.driver.PostgresDriver.api._

/**
  * Created by bsbuon on 8/13/17.
  */
object Config {

  val config =  ConfigFactory.load()

  lazy val db = Database.forConfig("application.db.postgres", config)

  lazy val dropSchema = config.getBoolean("application.db.dropSchema")

  lazy val poolingCallTime = config.getInt("application.pooling.time")

  lazy val yahooApiUrl = config.getString("yahoo.url")
  lazy val woeidSelect = config.getString("yahoo.select.woeid")
  lazy val forecastSelect = config.getString("yahoo.select.forecast")

  lazy val petitionLimit = config.getInt("yahoo.petition.limit")
}
