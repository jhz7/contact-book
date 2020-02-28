package co.com.addi.contact.book.infraestructure

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import play.api.libs.json.{Json, Writes}

object WebServerStub {

  private val port = 9001

  private val wireMockServer = new WireMockServer(port)

  def startStubServer(): Unit = {
    wireMockServer.start()
    WireMock.configureFor("localhost", wireMockServer.port())
  }

  def stopStubServer(): Unit = wireMockServer.stop()

  def mockGetRequest[T](url: String, body: T)(implicit requestWrites: Writes[T]): Unit = {
    val stringBody = Json.toJson(body).toString()
    stubFor(
      get(urlEqualTo(url))
        .willReturn(aResponse()
          .withStatus(200)
          .withBody(stringBody))
    )
  }
}
