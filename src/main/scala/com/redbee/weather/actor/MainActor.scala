package com.redbee.weather.actor

import java.util.concurrent.TimeUnit.SECONDS

import akka.actor.Actor
import akka.util.Timeout
import com.redbee.weather.controller.{BoardJsonProtocol, BoardRest, LocationRest}
import spray.routing.{HttpService, Route}

/**
  * Created by bsbuon on 8/13/17.
  */
class MainActor extends Actor
  with HttpService
  with BoardJsonProtocol
  with BoardRest
  with LocationRest{

  implicit val actorRefFactory = context

  implicit val timeout = Timeout(5, SECONDS)

  override def receive: Receive = runRoute(route)

  def route: Route = boardRoute ~ locationRoute
}
