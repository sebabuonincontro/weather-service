package com.redbee.weather

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.redbee.weather.actor.{GetNews, MainActor, PoolingActor}
import com.typesafe.scalalogging.LazyLogging
import spray.can.Http

import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}
import scala.concurrent.duration._
import com.redbee.weather.Config._

/**
  * Created by bsbuon on 8/13/17.
  */
object MainApp extends App with LazyLogging {

  implicit val actorSystem = ActorSystem()
  implicit val ec = actorSystem.dispatcher
  implicit val timeout = Timeout(FiniteDuration(5, TimeUnit.SECONDS))

  val weatherHandler = actorSystem.actorOf(Props(new MainActor), "weather-actor")

  //Main Actor
  lazy val server = IO(Http).ask(Http.Bind(listener = weatherHandler, interface = "localhost", port = 8080))
  server onComplete {
    case Success(_) => logger.info("Weather Service Initialized.")
    case Failure(error) => logger.error(s"Error while initialize weather service: $error")
  }

  DbSchema.handleSchemaCreation

  //Pooling Actor that receive notifications from Yahoo-Weather Service
  val poolingHandler = actorSystem.actorOf(Props(new PoolingActor), name = "pooling-actor")
  actorSystem.scheduler.schedule(0 seconds, poolingCallTime minutes, poolingHandler, GetNews)
}
