package com.redbee.weather.service

import com.redbee.weather.Board
import com.redbee.weather.Config._

import scala.concurrent.Future
import com.redbee.weather.Tables._
import slick.driver.PostgresDriver.api._

/**
  * Created by bsbuon on 8/13/17.
  */
object BoardService {

  def getBoards(): Future[Seq[Board]] = db.run(boardTable.result)

  def save(board: Board) : Future[Board] = db.run(boardTable returning boardTable +=  board)
}
