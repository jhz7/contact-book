package co.com.addi.contactbook.infraestructure.wsclients

import co.com.addi.contactbook.TestKit
import co.com.addi.contactbook.domain.models.{Error, Prospect}
import co.com.addi.contactbook.domain.types.{APPLICATION, TECHNICAL}
import co.com.addi.contactbook.factories.PersonFactory
import co.com.addi.contactbook.infraestructure.webserver.WebServerStub
import co.com.addi.contactbook.tools.FutureTool.awaitResult
import play.api.libs.json.Json
import play.api.libs.ws.ahc.{StandaloneAhcWSClient, StandaloneAhcWSRequest, StandaloneAhcWSResponse}

import scala.concurrent.Future

class RepublicIdentificationServiceTest extends TestKit {

  override def beforeEach(): Unit = WebServerStub.startStubServer()

  "RepublicIdentificationService" should {

    "Get person" when {

      "The prospect exists" must {
        "Return prospect instance" in {

          val dni = PersonFactory.createDni
          val personDto = PersonFactory.createPersonDto
          val wsClient = mock[StandaloneAhcWSClient]
          val request = mock[StandaloneAhcWSRequest]
          val response = mock[StandaloneAhcWSResponse]
          val futureResponse = Future.successful(response)

          doReturn(request).when(wsClient).url(anyString)
          doReturn(futureResponse).when(request).get()
          doReturn(200).when(response).status
          doReturn(Json.toJson(personDto).toString()).when(response).body

          val result = awaitResult(
            RepublicIdentificationService(wsClient).getProspectData(dni).value.runToFuture)

          result.map( _.exists(_.isInstanceOf[Prospect]) mustBe true )
        }
      }

      "The prospect does not exist" must {
        "Not return any value" in {

          val dni = PersonFactory.createDni
          val wsClient = mock[StandaloneAhcWSClient]
          val request = mock[StandaloneAhcWSRequest]
          val response = mock[StandaloneAhcWSResponse]
          val futureResponse = Future.successful(response)

          doReturn(request).when(wsClient).url(anyString)
          doReturn(futureResponse).when(request).get()
          doReturn(204).when(response).status

          val result = awaitResult(
            RepublicIdentificationService(wsClient).getProspectData(dni).value.runToFuture)

          result mustBe Right(None)
        }
      }

      "The retrieved data does not have a valid format" must {
        "Return an error related to bad json format" in {

          val dni = PersonFactory.createDni
          val badJsonObj = "{}"
          val wsClient = mock[StandaloneAhcWSClient]
          val request = mock[StandaloneAhcWSRequest]
          val response = mock[StandaloneAhcWSResponse]
          val futureResponse = Future.successful(response)

          doReturn(request).when(wsClient).url(anyString)
          doReturn(futureResponse).when(request).get()
          doReturn(200).when(response).status
          doReturn(badJsonObj).when(response).body

          val result = awaitResult(
            RepublicIdentificationService(wsClient).getProspectData(dni).value.runToFuture)

          result mustBe Left(Error(APPLICATION, "The json value do not have the expected format!"))
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

          val result = awaitResult(
            RepublicIdentificationService(wsClient).getProspectData(dni).value.runToFuture)

          result mustBe Left(Error(APPLICATION, errorMessage))
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

          val result = awaitResult(
            RepublicIdentificationService(wsClient).getProspectData(dni).value.runToFuture)

          result mustBe Left(Error(TECHNICAL, s"Has occurred an error getting data for person with id ${dni.number}"))
        }
      }

    }
  }

  override def afterEach(): Unit =  WebServerStub.stopStubServer()

}
