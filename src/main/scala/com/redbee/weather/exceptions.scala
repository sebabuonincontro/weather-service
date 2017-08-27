package com.redbee.weather

class BoardException extends RuntimeException
case class BoardNotFound() extends BoardException
case class LocationNotFound() extends BoardException
case class ForecastNotFound() extends BoardException
case class YahooRequestLimitExceeded() extends BoardException
