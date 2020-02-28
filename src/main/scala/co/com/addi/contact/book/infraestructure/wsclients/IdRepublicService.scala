package co.com.addi.contact.book.infraestructure.wsclients

import cats.data.{EitherT, Reader}
import co.com.addi.contact.book.application.constants._
import co.com.addi.contact.book.application.dtos.{APPLICATION, ErrorDto, PersonDto, TECHNICAL}
import co.com.addi.contact.book.application.types.{CustomEither, CustomEitherT}
import co.com.addi.contact.book.domain.models.Dni
import co.com.addi.contact.book.infraestructure.logger.Logger
import monix.eval.Task
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.ExecutionContext.Implicits._

trait IdRepublicService {

  val url = ""

  def getPerson(dni: Dni): Reader[StandaloneAhcWSClient, CustomEitherT[Option[PersonDto]]]

}

object IdRepublicService extends IdRepublicService {

  def getPerson(dni: Dni): Reader[StandaloneAhcWSClient, CustomEitherT[Option[PersonDto]]] = Reader {
    wsClient: StandaloneAhcWSClient =>
      EitherT{
        Task.deferFuture(
          wsClient.url(url).get().map( webResponse =>{
              if(webResponse.status == 200)
                Right(None)
              else if(webResponse.status == 204)
                Right(None)
              else
                Left(ErrorDto(APPLICATION, webResponse.body))
            })
            .recover[CustomEither[Option[PersonDto]]]{ case error: Throwable =>
              val message = errorMessageGettingPerson(dni.number)
              Logger.error(message, Some(error), getClass)
              Left(ErrorDto(TECHNICAL, message))
            }
        )
      }
  }

}
