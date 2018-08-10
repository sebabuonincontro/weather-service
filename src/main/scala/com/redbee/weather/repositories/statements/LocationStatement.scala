package com.redbee.weather.repositories.statements

import com.redbee.weather.Location
import doobie.util.query.Query0
import doobie.implicits._

trait LocationStatement {

  def getLocationsBy(id: Long): Query0[Location] = {
    sql"""select locationId
          from locations l
          inner join board_locations bl
          on bl.locationId = l.id
          where bl.boardId = $id""".query[Location]
  }
}
