package com.redbee.weather.repositories.statements

import com.redbee.weather.{Board, BoardWithLocations}
import doobie.free.connection.ConnectionIO
import doobie.implicits._
import doobie.util.query.Query0

trait BoardStatement {

  def getBoardByName(name: String): Query0[Board] = {
    sql"""select id, description
          from boards
          where description = $name""".query[Board]
  }
}
