package com.redbee.weather.service

import com.redbee.weather._
import com.redbee.weather.Config._
import com.redbee.weather.Tables._
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object LocationService {

  def getAll(): Future[Seq[Location]] = {
    val query = for {
      (locations, _) <- locationTable join
        boardLocationTable on (_.id === _.locationId)
    } yield locations

    db.run( query.result )
  }

  def save(location: Location, name: String): Future[Location] = {
    BoardService.getBoardBy(name).flatMap{
      case None => Future.failed(BoardNotFound())
      case Some(board) => for {
        newLocation <- saveLocation(location)
        boardLocation <- saveBoardLocation(newLocation.id.get, board.id.get)
      } yield newLocation
    }
  }

  def findBy(location: String): Future[Option[Location]] = {
    db.run(locationTable.filter { x => x.location.toUpperCase === location.toUpperCase}.result.headOption)
  }

  private def saveLocation(location: Location): Future[Location] = {
    val exists = findBy(location.location)
    exists.flatMap {
      case None => {
        for {
          locationCopy <- getDataFromYahoo(location)
          newLocation <- db.run(locationTable returning locationTable += locationCopy)
        } yield newLocation
      }
      case Some(l) => Future(l)
    }
  }

  private def getDataFromYahoo(location: Location): Future[Location] = {
    YahooWeatherClient.getWoeidFor(location.location).flatMap{
      case Some(value) => {
        val place = value.query.results.place
        Future(location.copy(woeid = place.woeid, location = place.name))
      }
      case None => Future.failed(LocationNotFound())
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
