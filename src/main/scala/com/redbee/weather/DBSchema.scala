package com.redbee.weather

import scala.concurrent.Await
import scala.util.Try
import slick.driver.PostgresDriver.api._

import scala.concurrent.duration._

import slick.driver.PostgresDriver

object DbSchema {

  val timeout = 2 minutes

  val schemas: List[PostgresDriver.DDL] = Tables.list.map(_.schema)

  def handleSchemaCreation: Unit = {
    if(Config.dropSchema) dropSchemas
    else createNonexistentSchemas
  }

  //If the schema exists, it is not re-created and the thrown exception is caught
  def createNonexistentSchemas: Unit =
    Try(Await.result(Config.db.run(DBIO.seq(schemas.map(x => x.create): _*)), timeout))

  def dropSchemas: Unit =
    Await.result(Config.db.run(DBIO.seq(schemas.reverse.map(x => x.drop): _*)), timeout)

}
