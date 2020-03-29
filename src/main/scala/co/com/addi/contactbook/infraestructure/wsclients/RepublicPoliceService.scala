package co.com.addi.contactbook.infraestructure.wsclients

import cats.data.EitherT
import co.com.addi.contactbook.application.commons.Logging
import co.com.addi.contactbook.application.dtos.CriminalRecordDto
import co.com.addi.contactbook.domain.aliases.{CustomEither, CustomEitherT}
import co.com.addi.contactbook.domain.contracts.wsclients.RepublicPoliceServiceContract
import co.com.addi.contactbook.domain.models.{Dni, Error}
import co.com.addi.contactbook.domain.types.TECHNICAL
import co.com.addi.contactbook.infraestructure.datasets.RepublicPoliceDataSet
import co.com.addi.contactbook.infraestructure.webserver.WebServerStub
import monix.eval.Task
import play.api.libs.ws.ahc.StandaloneAhcWSClient

class RepublicPoliceService(val wsClient: StandaloneAhcWSClient) extends RepublicPoliceServiceContract with WebClientHelper{

  private val webServerHost: String = "http://localhost:9001"
  private val webResourcePath: String => String = id => s"/republic-police-service/person/$id/criminal-record"

  def existsCriminalRecord( dni: Dni ): CustomEitherT[Boolean] = {

      Logging.info(s"Getting criminal record for prospect with id ${dni.number}", getClass)
      stubWebServer(dni.number)

      EitherT {
        Task.deferFutureAction( implicit sc =>
          wsClient.url(webServerHost + webResourcePath(dni.number)).get()
            .map( webResponse => processWebResponse[CriminalRecordDto](webResponse).map(_.isDefined) )
            .recover[CustomEither[Boolean]]{
              case error: Throwable =>
                val message = s"Has occurred an error getting criminal record for person with id ${dni.number}"
                Logging.error(message, getClass, Some(error))
                Left(Error(TECHNICAL, message))
            }
        )
      }
  }

  private def stubWebServer(id: String): Unit =
    RepublicPoliceDataSet.data.get(id) match {
      case Some(criminalRecordDto) => WebServerStub.mockSuccessGetRequest(webResourcePath(id), criminalRecordDto)
      case None                    => WebServerStub.mockSuccessNoContentGetRequest(webResourcePath(id))
    }
}
