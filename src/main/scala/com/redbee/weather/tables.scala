package com.redbee.weather

import java.sql.{Date, Timestamp}

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
  def woeid = column[Option[String]]("woeid")
  def location = column[String]("location")

  override def * = (id.?, woeid, location) <> (Location.tupled, Location.unapply)
}

class BoardLocationTable(tag: Tag) extends Table[BoardLocations](tag, "board_locations") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def boardId = column[Long]("board_id")
  def locationId = column[Long]("location_id")

  override def * = (id.?, boardId, locationId) <> (BoardLocations.tupled, BoardLocations.unapply)
}

class NewsTable(tag: Tag) extends Table[News](tag, "news") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def woeid = column[String]("woeid")
  def createDate = column[Timestamp]("create_date")
  def date = column[String]("date")
  def temp = column[String]("temp")
  def condition = column[String]("condition")

  override def * = (id.?, woeid, createDate, date, temp, condition) <> (News.tupled, News.unapply)
}

class ForecastTable(tag: Tag) extends Table[Forecast](tag, "forecasts") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def newsId = column[Long]("newsId")
  def woeid = column[String]("woeid")
  def date = column[String]("date")
  def high = column[Int]("high")
  def low = column[Int]("low")
  def forecast = column[String]("forecast")

  override def * = (id.?, newsId, woeid, date, high, low, forecast) <> (Forecast.tupled, Forecast.unapply)
}

class RequestLimitTable(tag: Tag) extends Table[RequestLimit](tag, "request_limit") {
  def date = column[Timestamp]("date")
  def quantity = column[Int]("quantity")

  def * = (date, quantity) <> (RequestLimit.tupled, RequestLimit.unapply)
}

object Tables {
  val boardTable = TableQuery[BoardTable]
  val locationTable = TableQuery[LocationTable]
  val boardLocationTable = TableQuery[BoardLocationTable]
  val newsTable = TableQuery[NewsTable]
  val forecastTable = TableQuery[ForecastTable]
  val requestLimitTable = TableQuery[RequestLimitTable]

  val list = List(boardTable, locationTable, boardLocationTable, newsTable, forecastTable, requestLimitTable)
}