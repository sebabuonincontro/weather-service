package com.redbee.weather

class WeatherMessage(code: Int, description: String) extends RuntimeException
case class BoardNotFound() extends WeatherMessage(100, "Board Not Found.")
