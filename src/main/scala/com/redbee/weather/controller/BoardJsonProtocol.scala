package com.redbee.weather.controller

import com.redbee.weather.{MainBody, _}
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait BoardJsonProtocol extends DefaultJsonProtocol
  with SprayJsonSupport {

  implicit val boardFormat = jsonFormat2(Board)
  implicit val locationFormat = jsonFormat3(Location)
  implicit val boardLocationFormat = jsonFormat2(BoardWithLocations)
  implicit val newsFormat = jsonFormat5(News)
  implicit val forecastFormat = jsonFormat7(Forecast)
  implicit val newFormat = jsonFormat3(LocationWithNewsAndForecasts)

  implicit val placeFormat = jsonFormat2(PlaceResponse)
  implicit val woeidFormat = jsonFormat1(WoeidResponse)
  implicit val bodyFormat = jsonFormat1(QueryBody[WoeidResponse])
  implicit val query1Format = jsonFormat1(MainBody[WoeidResponse])

  implicit val forecastsFormat = jsonFormat6(ForecastResponse)
  implicit val conditionFormat = jsonFormat4(ConditionResponse)
  implicit val itemFormat = jsonFormat3(ItemResponse)
  implicit val channelFormat = jsonFormat1(ChannelResponse)
  implicit val resultFormat = jsonFormat1(ResultResponse)

  implicit val query2Format = jsonFormat1(QueryBody[ResultResponse])
  implicit val mainFormat = jsonFormat1(MainBody[ResultResponse])

}
