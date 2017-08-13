package com.redbee.weather.controller

import com.redbee.weather.{Board, MainActor}
import com.redbee.weather.service.BoardService
import spray.http.StatusCodes
import spray.routing.Route

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * Created by bsbuon on 8/13/17.
  */
trait BoardRest{

  self : MainActor =>

  val boardPath = "boards"

  private def getAll =
    get {
      pathPrefix(boardPath){
        onComplete(BoardService.getBoards()){
          case Success(list) => complete(StatusCodes.OK, list)
          case Failure(error) => complete(StatusCodes.InternalServerError, error)
        }
      }
    }

  private def save =
    post {
      pathPrefix(boardPath){
        entity(as[Board]){ board =>
          onComplete(BoardService.save(board)){
            case Success(newBoard) => complete(StatusCodes.Created, newBoard)
            case Failure(error) => complete(StatusCodes.InternalServerError, error)
          }
        }
      }
    }

  val boardRoute: Route = getAll ~ save
}