package co.com.addi.contact.book.infraestructure.wsclients

import cats.data.{EitherT, Reader}
import co.com.addi.contact.book.application.dtos.{CriminalRecordDto, ErrorDto, TECHNICAL}
import co.com.addi.contact.book.application.types.{CustomEither, CustomEitherT}
import co.com.addi.contact.book.domain.models.Dni
import co.com.addi.contact.book.infraestructure.databases.RepublicPoliceDatabase
import co.com.addi.contact.book.infraestructure.logger.Logging
import co.com.addi.contact.book.infraestructure.webserver.WebServerStub
import monix.eval.Task
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.ExecutionContext.Implicits._

trait RepublicPoliceService {

  val webServerHost: String
  val webResourcePath: String => String

  def getCriminalRecord(dni: Dni): Reader[StandaloneAhcWSClient, CustomEitherT[Option[CriminalRecordDto]]]

}

object RepublicPoliceService extends RepublicPoliceService with WebClientHelper{

  override val webServerHost: String = "http://localhost:9001"

  override val webResourcePath: String => String = id => s"/republic-police-service/person/$id/criminal-record"

  def getCriminalRecord( dni: Dni ): Reader[StandaloneAhcWSClient, CustomEitherT[Option[CriminalRecordDto]]] = Reader {
    wsClient: StandaloneAhcWSClient =>

      stubWebServer(dni.number)

      EitherT {
        Task.deferFuture(
          wsClient.url(webServerHost + webResourcePath(dni.number)).get()
            .map( webResponse => processWebResponse[CriminalRecordDto](webResponse) )
            .recover[CustomEither[Option[CriminalRecordDto]]]{
              case error: Throwable =>
                val message = s"Has occurred an error getting criminal record for person with id ${dni.number}"
                Logging.error(message, Some(error), getClass)
                Left(ErrorDto(TECHNICAL, message))
            }
        )
      }
  }

  private def stubWebServer(id: String): Unit =
    RepublicPoliceDatabase.data.get(id) match {
      case Some(criminalRecordDto) => WebServerStub.mockSuccessGetRequest(webResourcePath(id), criminalRecordDto)
      case None                    => WebServerStub.mockSuccessNoContentGetRequest(webResourcePath(id))
    }
}
