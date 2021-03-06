package com.redbee.weather

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, OneForOneStrategy, Props, SupervisorStrategy}
import akka.io.IO
import akka.pattern.{Backoff, BackoffSupervisor, ask}
import akka.util.Timeout
import com.redbee.weather.actor.{MainActor, PoolingActor}
import com.typesafe.scalalogging.LazyLogging
import spray.can.Http

import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}
import scala.concurrent.duration._
import com.redbee.weather.Config._
import com.redbee.weather.actor.messages.GetNews


/**
  * Created by bsbuon on 8/13/17.
  */
object MainApp extends App with LazyLogging {

  implicit val actorSystem = ActorSystem()
  implicit val ec = actorSystem.dispatcher
  implicit val timeout = Timeout(FiniteDuration(5, TimeUnit.SECONDS))

  val weatherHandler = actorSystem.actorOf(Props(new MainActor), "weather-actor")

  //Main Actor
  lazy val server = IO(Http).ask(Http.Bind(listener = weatherHandler, interface = "localhost", port = 8090))
  server onComplete {
    case Success(_) => logger.info("Weather Service Initialized.")
    case Failure(error) => logger.error(s"Error while initialize weather service: $error")
  }

  DbSchema.createNonExistentTables

  //Pooling Actor that receive notifications from Yahoo-Weather Service
  val supervisorProps = BackoffSupervisor.props(
    Backoff
      .onFailure(
        Props(new PoolingActor),
        childName = "pooling-actor",
        minBackoff = 3 seconds,
        maxBackoff = 30 seconds,
        randomFactor = 0.2
      )
      .withAutoReset(10 seconds) // reset if the child does not throw any errors within 10 seconds
      .withSupervisorStrategy(OneForOneStrategy() {
        case error ⇒
          logger.error("Restart Consumer", error)
          SupervisorStrategy.Restart
      }))

  val supervisor = actorSystem.actorOf(supervisorProps, "supervisor")

  val poolingHandler = actorSystem.actorOf(Props(new PoolingActor), name = "pooling-actor")
  actorSystem.scheduler.schedule(0 seconds, poolingCallTime minutes, supervisor, GetNews)
}
