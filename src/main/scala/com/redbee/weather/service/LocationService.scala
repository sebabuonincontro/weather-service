package com.redbee.weather.service

import com.redbee.weather._
import com.redbee.weather.Config._
import com.redbee.weather.Tables._
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object LocationService {

  def getBy(name: String): Future[Seq[Location]] = {
    val query = for {
      board <- boardTable.filter( _.description like name )
      (locations, _) <- locationTable join
        boardLocationTable on (_.id === _.locationId) filter(_._2.boardId === board.id)
    } yield locations

    db.run( query.result )
  }

  def save(location: Location, name: String): Future[Location] = {
    findBoardBy(name).flatMap{
      case None => Future.failed(BoardNotFound())
      case Some(board) => for {
        newLocation <- saveLocation(location)
        _ <- saveBoardLocation(newLocation.id.get, board.id.get)
      } yield newLocation
    }
  }

  private def findBoardBy(name: String): Future[Option[Board]] = {
    db.run(boardTable.filter(_.description like name).result.headOption)
  }

  private def saveLocation(location: Location): Future[Location] = {
    val exists = db.run(locationTable.filter(_.location.toUpperCase like location.location.toUpperCase).result.headOption)
    exists.flatMap {
      case None => db.run(locationTable returning locationTable += location)
      case Some(l) => Future(l)
    }
  }

  private def saveBoardLocation(locationId: Long, boardId: Long): Future[BoardLocations] = {
    val exists = db.run(boardLocationTable.filter(r => r.boardId === boardId && r.locationId === locationId).result.headOption)
    exists.flatMap{
      case Some(boardLocation) => Future(boardLocation)
      case None => db.run(boardLocationTable returning boardLocationTable += BoardLocations(None, boardId, locationId))
    }
  }
}
