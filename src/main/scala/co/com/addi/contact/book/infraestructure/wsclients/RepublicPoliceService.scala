package co.com.addi.contact.book.infraestructure.wsclients

import cats.data.{EitherT, Reader}
import co.com.addi.contact.book.application.constants._
import co.com.addi.contact.book.application.dtos.{APPLICATION, CriminalRecordDto, ErrorDto, TECHNICAL}
import co.com.addi.contact.book.application.services.JsonSerializationService
import co.com.addi.contact.book.application.types.{CustomEither, CustomEitherT}
import co.com.addi.contact.book.domain.models.Dni
import co.com.addi.contact.book.infraestructure.databases.RepublicPoliceDatabase
import co.com.addi.contact.book.infraestructure.logger.Logger
import co.com.addi.contact.book.infraestructure.webserver.HttpStubbingManager
import monix.eval.Task
import play.api.libs.ws.ahc.{StandaloneAhcWSClient, StandaloneAhcWSRequest}

import scala.concurrent.ExecutionContext.Implicits._

trait RepublicPoliceService extends HttpStubbingManager{

  val webServerUrl: String

  def getCriminalRecord(dni: Dni): Reader[StandaloneAhcWSClient, CustomEitherT[Option[CriminalRecordDto]]]

}

object RepublicPoliceService extends RepublicPoliceService {

  override val webServerErrorMessage: String = "The republic police server has generated an error..."

  override val webServerUrl: String = republicPoliceWebServerUrl

  def getCriminalRecord( dni: Dni ): Reader[StandaloneAhcWSClient, CustomEitherT[Option[CriminalRecordDto]]] = Reader {
    wsClient: StandaloneAhcWSClient =>

      stubWebServer(dni.number)
      val getRequest = wsClient.url(webServerUrl).get()

      EitherT {
        Task.deferFuture(
          getRequest.map( processWebResponse )
            .recover[CustomEither[Option[CriminalRecordDto]]]{
              case error: Throwable =>
                val message = s"Has occurred an error getting criminal record for person with id ${dni.number}"
                Logger.error(message, Some(error), getClass)
                Left(ErrorDto(TECHNICAL, message))
            }
        )
      }
  }

  private def processWebResponse(webResponse: StandaloneAhcWSRequest#Response): CustomEither[Option[CriminalRecordDto]] ={
    if(webResponse.status == 200)
      JsonSerializationService.deserialize[CriminalRecordDto](webResponse.body).map(Some(_))
    else if(webResponse.status == 204)
      Right(None)
    else
      Left(ErrorDto(APPLICATION, webResponse.body))
  }

  private def stubWebServer(id: String): Unit =
    RepublicPoliceDatabase.data.get(id)
      .foreach(criminalRecordDto => stubbingRandomlyWebServer(webServerUrl, criminalRecordDto))
}
