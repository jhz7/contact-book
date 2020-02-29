package co.com.addi.contact.book.infraestructure.wsclients

import co.com.addi.contact.book.TestKit
import co.com.addi.contact.book.application.dtos.{APPLICATION, CriminalRecordDto, ErrorDto, TECHNICAL}
import co.com.addi.contact.book.factories.{CriminalRecordFactory, PersonFactory}
import co.com.addi.contact.book.infraestructure.webserver.WebServerStub
import co.com.addi.contact.book.tools.FutureTool
import play.api.libs.json.Json
import play.api.libs.ws.ahc.{StandaloneAhcWSClient, StandaloneAhcWSRequest, StandaloneAhcWSResponse}

import scala.concurrent.Future

class RepublicPoliceServiceTest extends TestKit {

  before( WebServerStub.startStubServer() )

  "RepublicPoliceService" should {

    "Get the criminal record" when {

      "The criminal record exists" must {
        "Return CriminalRecord instance" in {

          val dni = PersonFactory.createDni
          val criminalRecordDto = CriminalRecordFactory.getInstance
          val wsClient = mock[StandaloneAhcWSClient]
          val request = mock[StandaloneAhcWSRequest]
          val response = mock[StandaloneAhcWSResponse]
          val futureResponse = Future.successful(response)

          doReturn(request).when(wsClient).url(anyString)
          doReturn(futureResponse).when(request).get()
          doReturn(200).when(response).status
          doReturn(Json.toJson(criminalRecordDto).toString()).when(response).body

          val result = FutureTool.awaitResult(RepublicPoliceService.getCriminalRecord(dni).run(wsClient).value.runToFuture)

          result.map( _.exists(_.isInstanceOf[CriminalRecordDto]) mustBe true )
        }
      }

      "The criminal record does not exist" must {
        "Not return any value" in {

          val dni = PersonFactory.createDni
          val wsClient = mock[StandaloneAhcWSClient]
          val request = mock[StandaloneAhcWSRequest]
          val response = mock[StandaloneAhcWSResponse]
          val futureResponse = Future.successful(response)

          doReturn(request).when(wsClient).url(anyString)
          doReturn(futureResponse).when(request).get()
          doReturn(204).when(response).status

          val result = FutureTool.awaitResult(RepublicPoliceService.getCriminalRecord(dni).run(wsClient).value.runToFuture)

          result mustBe Right(None)
        }
      }

      "The server returns an error" must {
        "Return the generated error" in {

          val dni = PersonFactory.createDni
          val errorMessage = "Fake server error"
          val wsClient = mock[StandaloneAhcWSClient]
          val request = mock[StandaloneAhcWSRequest]
          val response = mock[StandaloneAhcWSResponse]
          val futureResponse = Future.successful(response)

          doReturn(request).when(wsClient).url(anyString)
          doReturn(futureResponse).when(request).get()
          doReturn(500).when(response).status
          doReturn(errorMessage).when(response).body

          val result = FutureTool.awaitResult(RepublicPoliceService.getCriminalRecord(dni).run(wsClient).value.runToFuture)

          result mustBe Left(ErrorDto(APPLICATION, errorMessage))
        }
      }

      "The get request fails" must {
        "Return the error" in {

          val dni = PersonFactory.createDni
          val wsClient = mock[StandaloneAhcWSClient]
          val request = mock[StandaloneAhcWSRequest]
          val futureResponse = Future.failed(new Exception)

          doReturn(request).when(wsClient).url(anyString)
          doReturn(futureResponse).when(request).get()

          val result = FutureTool.awaitResult(RepublicPoliceService.getCriminalRecord(dni).run(wsClient).value.runToFuture)

          result mustBe Left(ErrorDto(TECHNICAL, s"Has occurred an error getting criminal record for person with id ${dni.number}"))
        }
      }

    }
  }

  after( WebServerStub.stopStubServer() )

}
