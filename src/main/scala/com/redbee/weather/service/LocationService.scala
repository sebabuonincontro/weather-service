package com.redbee.weather.service

import com.redbee.weather.Location
import com.redbee.weather.Config._
import com.redbee.weather.Tables._
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

object LocationService {

  def getBy(name: String): Future[Seq[Location]] = {
    val query = for {
      board <- boardTable.filter( _.description === name )
      locations <- locationTable join boardLocationTable on (_.id === _.locationId) filter(_._2.boardId === board.id)
    } yield locations._1

    db.run( query.result )
  }

}
