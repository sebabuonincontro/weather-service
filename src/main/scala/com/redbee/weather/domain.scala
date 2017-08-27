package com.redbee.weather

import java.sql.Timestamp

case class Board(
  id: Option[Long],
  description: String)

case class Location(
  id: Option[Long],
  woeid: Option[String],
  location: String)

case class BoardLocations(
  id: Option[Long],
  boardId: Long,
  locationId: Long)

case class BoardWithLocations(
  board: Board,
  locations: Seq[Location])

case class News(
  id: Option[Long],
  woeid: String,
  createDate: Timestamp,
  date: String,
  temp: String,
  condition: String)

case class Forecast(
  id: Option[Long],
  newsId: Long,
  woeid: String,
  date: String,
  high: Int,
  low: Int,
  forecast: String)

case class RequestLimit(
  date: Timestamp,
  quantity: Int)

//Entities used for the Rest service result
case class LocationWithNewsAndForecasts( location: Location, news: News, forecasts: Seq[Forecast])

case class MainBody[T](query: QueryBody[T])

case class QueryBody[T](results: T)

case class WoeidResponse(place: PlaceResponse)

case class PlaceResponse(name: String, woeid: String)

case class ResultResponse(channel: ChannelResponse)

case class ChannelResponse(item: ItemResponse)

case class ItemResponse(
  title: String,
  condition: ConditionResponse,
  forecast: Seq[ForecastResponse])

case class ConditionResponse(
  code: String,
  date: String,
  temp: String,
  text: String)

case class ForecastResponse(
  code: String,
  date: String,
  day: String,
  high: String,
  low: String,
  text: String)