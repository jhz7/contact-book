package co.com.addi.contact.book.infraestructure.wsclients

import cats.data.{EitherT, Reader}
import co.com.addi.contact.book.application.constants._
import co.com.addi.contact.book.application.dtos.{APPLICATION, ErrorDto, PersonDto, TECHNICAL}
import co.com.addi.contact.book.application.services.JsonSerializationService
import co.com.addi.contact.book.application.types.{CustomEither, CustomEitherT}
import co.com.addi.contact.book.domain.models.{Dni, Person}
import co.com.addi.contact.book.infraestructure.databases.RepublicIdentificationDataBase
import co.com.addi.contact.book.infraestructure.logger.Logging
import co.com.addi.contact.book.infraestructure.transformers.PersonTransformer
import co.com.addi.contact.book.infraestructure.webserver.WebServerStub
import monix.eval.Task
import play.api.libs.ws.ahc.{StandaloneAhcWSClient, StandaloneAhcWSRequest}

import scala.concurrent.ExecutionContext.Implicits._

trait RepublicIdentificationService {

  val webServerUrl: String

  def getPerson(dni: Dni): Reader[StandaloneAhcWSClient, CustomEitherT[Option[Person]]]

}

object RepublicIdentificationService extends RepublicIdentificationService {

  override val webServerUrl: String = republicIdentificationWebServerUrl

  def getPerson(dni: Dni): Reader[StandaloneAhcWSClient, CustomEitherT[Option[Person]]] = Reader {
    wsClient: StandaloneAhcWSClient =>

      stubWebServer(dni.number)

      EitherT {
        Task.deferFuture(
          wsClient.url(s"http://localhost:9001/republic-id-service/person/${dni.number}/info").get()
            .map( processWebResponse )
            .recover[CustomEither[Option[Person]]]{
              case error: Throwable =>
                val message = s"Has occurred an error getting data for person with id ${dni.number}"
                Logging.error(message, Some(error), getClass)
                Left(ErrorDto(TECHNICAL, message))
            }
        )
      }
  }

  private def processWebResponse(webResponse: StandaloneAhcWSRequest#Response): CustomEither[Option[Person]] ={
    if(webResponse.status == 200)
      JsonSerializationService.deserialize[PersonDto](webResponse.body)
        .map(persoData => Some(PersonTransformer.toPerson(persoData)))
    else if(webResponse.status == 204)
      Right(None)
    else
      Left(ErrorDto(APPLICATION, webResponse.body))
  }

  private def stubWebServer(id: String): Unit =
    RepublicIdentificationDataBase.data.get(id) match {
      case Some(personDto) => WebServerStub.mockSuccessGetRequest(s"/republic-id-service/person/$id/info", personDto)
      case None            => WebServerStub.mockSuccessNoContentGetRequest(s"/republic-id-service/person/$id/info")
    }

}
