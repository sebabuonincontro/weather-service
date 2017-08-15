package com.redbee.weather.actor

import java.time.Instant

import akka.actor.{Actor, ActorLogging}

class PoolingActor extends Actor
  with ActorLogging {

  override def receive = {
    case GetNews => getNews
  }

  private def getNews() = {
    //TODO this is a sample.
    log.info("Get Locations News at: " + Instant.now().toString )
  }
}

case class GetNews()