package com.redbee.weather

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import spray.can.Http

import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}

/**
  * Created by bsbuon on 8/13/17.
  */
object MainApp extends App with LazyLogging {

  implicit val actorSystem = ActorSystem()
  implicit val ec = actorSystem.dispatcher
  implicit val timeout = Timeout(FiniteDuration(10, TimeUnit.SECONDS))

  val weatherHandler = actorSystem.actorOf(Props(new MainActor), "weather-actor")

  lazy val server = IO(Http).ask(Http.Bind(listener = weatherHandler, interface = "localhost", port = 8080))
  server onComplete {
    case Success(_) => logger.info("Weather Service Initialize...")
    case Failure(error) => logger.error(s"Error while initialize weather service: $error")
  }

}
