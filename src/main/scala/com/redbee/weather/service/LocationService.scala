package com.redbee.weather.service

import com.redbee.weather.{Board, BoardLocations, Location}
import com.redbee.weather.Config._
import com.redbee.weather.Tables._
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

object LocationService {

  def getBy(name: String): Future[Seq[Location]] = {
    val query = for {
      board <- boardTable.filter( _.description === name )
      locations <- locationTable join boardLocationTable on (_.id === _.locationId) filter(_._2.boardId === board.id)
    } yield locations._1

    db.run( query.result )
  }

  def save(location: Location, name: String): Future[Location] = {
    findBoardBy(name).flatMap{
      case Some(board) => for {
        newLocation <- saveLocation(location)
        _ <- saveBoardLocation(newLocation, board)
      } yield newLocation
    }
  }

  private def findBoardBy(name: String): Future[Option[Board]] = {
    db.run( boardTable.filter(_.description === name).result.headOption)
  }

  private def saveLocation(location: Location): Future[Location] = {
    db.run(locationTable returning locationTable += location)
  }

  private def saveBoardLocation(location: Location, board: Board): Future[BoardLocations] = {
    location.id -> board.id match {
      case (Some(locationId), Some(boardId)) => db.run(boardLocationTable returning boardLocationTable += BoardLocations(None, boardId, locationId))
    }
  }
}
