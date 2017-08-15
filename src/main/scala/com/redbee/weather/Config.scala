package com.redbee.weather

import com.typesafe.config.ConfigFactory
import slick.driver.PostgresDriver.api._

/**
  * Created by bsbuon on 8/13/17.
  */
object Config {

  val config =  ConfigFactory.load()

  lazy val db = Database.forConfig("application.db.postgres", config)

  lazy val poolingCallTime = config.getInt("application.pooling.time")
}
