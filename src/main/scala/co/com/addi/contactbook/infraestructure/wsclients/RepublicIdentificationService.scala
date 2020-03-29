package co.com.addi.contactbook.infraestructure.wsclients

import cats.data.EitherT
import co.com.addi.contactbook.application.commons.Logging
import co.com.addi.contactbook.application.dtos.PersonDto
import co.com.addi.contactbook.domain.aliases.{CustomEither, CustomEitherT}
import co.com.addi.contactbook.domain.contracts.wsclients.RepublicIdentificationServiceContract
import co.com.addi.contactbook.domain.models.{Contact, Dni, Error}
import co.com.addi.contactbook.domain.types.TECHNICAL
import co.com.addi.contactbook.infraestructure.databases.RepublicIdentificationDataBase
import co.com.addi.contactbook.infraestructure.transformers.PersonTransformer
import co.com.addi.contactbook.infraestructure.webserver.WebServerStub
import monix.eval.Task
import play.api.libs.ws.ahc.StandaloneAhcWSClient


class RepublicIdentificationService(val wsClient: StandaloneAhcWSClient) extends RepublicIdentificationServiceContract with WebClientHelper{

  private val webServerHost: String = "http://localhost:9001"
  private val webResourcePath: String => String = id => s"/republic-id-service/person/$id/info"

  def getPerson(dni: Dni): CustomEitherT[Option[Contact]] = {

      Logging.info(s"Getting personal information for prospect with id ${dni.number}", getClass)
      stubWebServer(dni.number)

      EitherT {
        Task.deferFutureAction( implicit sc =>

          wsClient.url(webServerHost + webResourcePath(dni.number)).get()
            .map( webResponse =>
              processWebResponse[PersonDto](webResponse).map(_.map( PersonTransformer.toPerson ))
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
