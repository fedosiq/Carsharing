package com.fedosique.carsharing.api

import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.ExceptionHandler
import com.fedosique.carsharing.CarsharingException


object CarsharingExceptionHandler {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

  val exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case e: CarsharingException => complete(BadRequest, ExceptionResponse(e.getMessage))
    }
}
