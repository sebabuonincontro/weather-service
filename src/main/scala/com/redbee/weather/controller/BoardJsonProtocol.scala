package com.redbee.weather.controller

import com.redbee.weather.{Board, Location}
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait BoardJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport  {
  implicit val boardFormat = jsonFormat2(Board)
  implicit val locationFormat = jsonFormat3(Location)
}
