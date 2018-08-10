package com.redbee.weather.repositories.dao

import cats.effect.IO
import com.redbee.weather.BoardWithLocations
import com.redbee.weather.repositories.DoobieLike
import com.redbee.weather.repositories.statements.{BoardStatement, LocationStatement}
import doobie.implicits._

object BoardDao extends DoobieLike
  with BoardStatement
  with LocationStatement {

  def getBoardAndLocations(name: String): IO[BoardWithLocations] = {
    val result = for {
      board <- getBoardByName(name).unique
      locations <- getLocationsBy(board.id.get).to[List]
    } yield BoardWithLocations(board, locations)

    result.transact(xa)
  }
}
