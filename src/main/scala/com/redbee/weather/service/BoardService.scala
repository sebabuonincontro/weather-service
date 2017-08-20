package com.redbee.weather.service

import com.redbee.weather.{Board, BoardWithLocations}
import com.redbee.weather.Config._

import scala.concurrent.Future
import com.redbee.weather.Tables._
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by bsbuon on 8/13/17.
  */
object BoardService {

  def getBoards(): Future[Seq[Board]] = db.run(boardTable.result)

  def getBoardWithLocationsBy(name: String): Future[Option[BoardWithLocations]] = {
    val query = for {
      ((board, _), locations) <- boardTable filter(_.description === name) joinLeft
        boardLocationTable on(_.id === _.boardId) joinLeft
        locationTable on(_._2.map(_.locationId) === _.id)
    } yield board -> locations

    db.run(query.result).map{ seq =>
      seq.map{ tuple =>
        BoardWithLocations(tuple._1, seq.flatMap(_._2).toList)
      }.headOption
    }
  }

  def getBoardBy(name: String): Future[Option[Board]] = {
    db.run(boardTable.filter(_.description like name).result.headOption)
  }

  def save(board: Board) : Future[Board] =
    db.run(boardTable returning boardTable +=  board)

  def remove(boardId: Long): Future[Long] = {
    for {
      bl <- db.run(boardLocationTable.filter(_.boardId === boardId).delete)
      board <- db.run(boardTable.filter(_.id === boardId).delete)
    } yield board
  }
}
