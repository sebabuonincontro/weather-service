package com.redbee.weather

/**
  * Created by bsbuon on 8/13/17.
  */
case class Board(
  id: Option[Long],
  description: String)

case class Location(
  id: Option[Long],
  woeid: String,
  location: String)

case class BoardLocations(
  id: Option[Long],
  boardId: Long,
  locationId: Long)

case class BoardWithLocations(
  board: Board,
  locations: Seq[Location])