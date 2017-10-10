package com.redbee.weather

import scala.concurrent.Await
import slick.driver.MySQLDriver.api._

import scala.concurrent.duration.Duration
import slick.jdbc.meta.MTable
import scala.concurrent.ExecutionContext.Implicits.global

object DbSchema {

  //If the table don't exists, then the table is created.
  def createNonExistentTables: Unit = {
    val existing = Config.db.run(MTable.getTables)
    val f = existing.flatMap( v => {
      val names = v.map(mt => mt.name.name)
      val createIfNotExist = Tables.list.filter( table =>
        !names.contains(table.baseTableRow.tableName)).map(_.schema.create)
      Config.db.run(DBIO.sequence(createIfNotExist))
    })
    Await.result(f, Duration.Inf)
  }

}
