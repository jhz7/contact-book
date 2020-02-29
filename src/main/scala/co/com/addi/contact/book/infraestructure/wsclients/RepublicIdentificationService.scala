package co.com.addi.contact.book.infraestructure.wsclients

import cats.data.{EitherT, Reader}
import co.com.addi.contact.book.application.constants._
import co.com.addi.contact.book.application.dtos.{APPLICATION, ErrorDto, PersonDto, TECHNICAL}
import co.com.addi.contact.book.application.services.JsonSerializationService
import co.com.addi.contact.book.application.types.{CustomEither, CustomEitherT}
import co.com.addi.contact.book.domain.models.Dni
import co.com.addi.contact.book.infraestructure.logger.Logger
import co.com.addi.contact.book.infraestructure.{HttpStubbingManager, RepublicIdentificationDataBase}
import monix.eval.Task
import play.api.libs.ws.ahc.{StandaloneAhcWSClient, StandaloneAhcWSRequest}

import scala.concurrent.ExecutionContext.Implicits._

trait RepublicIdentificationService extends HttpStubbingManager {

  val webServerUrl: String

  def getPerson(dni: Dni): Reader[StandaloneAhcWSClient, CustomEitherT[Option[PersonDto]]]

}

object RepublicIdentificationService extends RepublicIdentificationService {

  override val webServerUrl: String = idRepublicWebServerUrl

  def getPerson(dni: Dni): Reader[StandaloneAhcWSClient, CustomEitherT[Option[PersonDto]]] = Reader {
    wsClient: StandaloneAhcWSClient =>

      stubWebServer(dni.number)
      val getRequest = wsClient.url(webServerUrl).get()

      EitherT {
        Task.deferFuture(
          getRequest.map( processWebResponse )
            .recover[CustomEither[Option[PersonDto]]]{
              case error: Throwable =>
                val message = s"Has occurred an error getting data for person with id ${dni.number}"
                Logger.error(message, Some(error), getClass)
                Left(ErrorDto(TECHNICAL, message))
            }
        )
      }
  }

  private def processWebResponse(webResponse: StandaloneAhcWSRequest#Response): CustomEither[Option[PersonDto]] ={
    if(webResponse.status == 200)
      JsonSerializationService.deserialize[PersonDto](webResponse.body).map(Some(_))
    else if(webResponse.status == 204)
      Right(None)
    else
      Left(ErrorDto(APPLICATION, webResponse.body))
  }

  private def stubWebServer(id: String): Unit =
    RepublicIdentificationDataBase.data.get(id)
      .foreach(personDto => stubbingRandomlyWebServer(webServerUrl, personDto))

  override val webServerErrorMessage: String = "The server has generated an error..."

}
