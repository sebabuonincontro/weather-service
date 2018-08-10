package com.redbee.weather.repositories

import cats.effect.IO
import com.typesafe.config.ConfigFactory
import doobie._

trait DoobieLike {

  val config = ConfigFactory.load()
  val xa = Transactor.fromDriverManager[IO](
    config.getString("application.db.mysql.driver"),
    config.getString("application.db.mysql.url"),
    config.getString("application.db.mysql.user"),
    config.getString("application.db.mysql.password"))

}
