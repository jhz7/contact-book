package co.com.addi.contact.book.infraestructure.wsclients

import cats.data.{EitherT, Reader}
import co.com.addi.contact.book.application.commons.Logging
import co.com.addi.contact.book.application.dtos.{ErrorDto, PersonDto, TECHNICAL}
import co.com.addi.contact.book.application.types.{CustomEither, CustomEitherT}
import co.com.addi.contact.book.domain.models.{Dni, Person}
import co.com.addi.contact.book.infraestructure.databases.RepublicIdentificationDataBase
import co.com.addi.contact.book.infraestructure.transformers.PersonTransformer
import co.com.addi.contact.book.infraestructure.webserver.WebServerStub
import monix.eval.Task
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.ExecutionContext.Implicits._

trait RepublicIdentificationService {

  val webServerHost: String
  val webResourcePath: String => String

  def getPerson(dni: Dni): Reader[StandaloneAhcWSClient, CustomEitherT[Option[Person]]]

}

object RepublicIdentificationService extends RepublicIdentificationService with WebClientHelper{

  override val webServerHost: String = "http://localhost:9001"

  override val webResourcePath: String => String = id => s"/republic-id-service/person/$id/info"

  def getPerson(dni: Dni): Reader[StandaloneAhcWSClient, CustomEitherT[Option[Person]]] = Reader {
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
            .recover[CustomEither[Option[Person]]]{
              case error: Throwable =>
                val message = s"Has occurred an error getting data for person with id ${dni.number}"
                Logging.error(message, Some(error), getClass)
                Left(ErrorDto(TECHNICAL, message))
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
