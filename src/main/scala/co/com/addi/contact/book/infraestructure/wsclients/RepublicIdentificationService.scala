package co.com.addi.contact.book.infraestructure.wsclients

import cats.data.{EitherT, Reader}
import co.com.addi.contact.book.application.commons.Logging
import co.com.addi.contact.book.application.dtos.{PersonDto, TECHNICAL}
import co.com.addi.contact.book.application.types.{CustomEither, CustomEitherT}
import co.com.addi.contact.book.domain.models.{Contact, Dni, Error, TECHNICAL}
import co.com.addi.contact.book.infraestructure.databases.RepublicIdentificationDataBase
import co.com.addi.contact.book.infraestructure.transformers.PersonTransformer
import co.com.addi.contact.book.infraestructure.webserver.WebServerStub
import monix.eval.Task
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.ExecutionContext.Implicits._

trait RepublicIdentificationService {

  val webServerHost: String
  val webResourcePath: String => String

  def getPerson(dni: Dni): Reader[StandaloneAhcWSClient, CustomEitherT[Option[Contact]]]

}

object RepublicIdentificationService extends RepublicIdentificationService with WebClientHelper{

  override val webServerHost: String = "http://localhost:9001"

  override val webResourcePath: String => String = id => s"/republic-id-service/person/$id/info"

  def getPerson(dni: Dni): Reader[StandaloneAhcWSClient, CustomEitherT[Option[Contact]]] = Reader {
    wsClient: StandaloneAhcWSClient =>
      Logging.info(s"Getting personal information for prospect with id ${dni.number}", getClass)

      stubWebServer(dni.number)

      EitherT {
        Task.deferFuture(
          wsClient.url(webServerHost + webResourcePath(dni.number)).get()
            .map( webResponse =>
              processWebResponse[PersonDto](webResponse)
                .map(_.map( PersonTransformer.toPerson ))
            )
            .recover[CustomEither[Option[Contact]]]{
              case error: Throwable =>
                val message = s"Has occurred an error getting data for person with id ${dni.number}"
                Logging.error(message, getClass, Some(error))
                Left(Error(TECHNICAL, message))
            }
        )
      }
  }

  private def stubWebServer(id: String): Unit =
    RepublicIdentificationDataBase.data.get(id) match {
      case Some(personDto) => WebServerStub.mockSuccessGetRequest(webResourcePath(id), personDto)
      case None            => WebServerStub.mockSuccessNoContentGetRequest(webResourcePath(id))
    }

}
