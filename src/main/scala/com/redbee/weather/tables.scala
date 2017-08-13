package com.redbee.weather

import slick.driver.PostgresDriver.api._
import slick.lifted.Tag
/**
  * Created by bsbuon on 8/13/17.
  */
class BoardTable(tag:Tag) extends Table[Board](tag, "boards"){
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def description = column[String]("description")

  override def * = (id.?, description) <> (Board.tupled, Board.unapply)
}

class LocationTable(tag: Tag) extends Table[Location](tag, "locations"){
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def woeid = column[String]("woeid")
  def location = column[String]("location")

  override def * = (id.?, woeid, location) <> (Location.tupled, Location.unapply)
}

class BoardLocationTable(tag: Tag) extends Table[BoardLocations](tag, "board_locations") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def boardId = column[Long]("board_id")
  def locationId = column[Long]("location_id")

  override def * = (id.?, boardId, locationId) <> (BoardLocations.tupled, BoardLocations.unapply)
}

object Tables {
  val boardTable = TableQuery[BoardTable]
  val locationTable = TableQuery[LocationTable]
  val boardLocationTable = TableQuery[BoardLocationTable]
}