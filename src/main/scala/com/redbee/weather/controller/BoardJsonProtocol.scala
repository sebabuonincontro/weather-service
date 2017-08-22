package com.redbee.weather.controller

import java.sql.Timestamp

import com.redbee.weather.{MainBody, _}
import spray.httpx.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, DeserializationException, JsNumber, JsObject, JsValue, RootJsonFormat}

trait BoardJsonProtocol extends DefaultJsonProtocol
  with SprayJsonSupport {

  implicit val timestampFormat = TimestampFormat

  implicit val boardFormat = jsonFormat2(Board)
  implicit val locationFormat = jsonFormat3(Location)
  implicit val boardLocationFormat = jsonFormat2(BoardWithLocations)
  implicit val newsFormat = jsonFormat6(News)
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

object TimestampFormat extends RootJsonFormat[Timestamp] {
  def write(obj: Timestamp) = {
    JsObject("date" -> JsNumber(obj.getTime))
  }
  def read(json: JsValue) = {
    json.asJsObject().getFields("date") match {
      case Seq(JsNumber(time)) => new Timestamp(time.toLong)
      case _ => throw DeserializationException("Date expected")
    }
  }
}
