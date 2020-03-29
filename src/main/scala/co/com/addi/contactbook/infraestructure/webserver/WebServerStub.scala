package co.com.addi.contactbook.infraestructure.webserver

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, stubFor, urlEqualTo}
import play.api.libs.json.{Json, Writes}

import scala.util.Random

object WebServerStub {

  private val port = 9001

  private val wireMockServer = new WireMockServer(port)

  private def getLatency: Int = Random.nextInt(1000)

  def startStubServer(): Unit = {
    wireMockServer.start()
  }

  def stopStubServer(): Unit = wireMockServer.stop()

  def mockSuccessGetRequest[T](url: String, body: T)(implicit requestWrites: Writes[T]): Unit = {
    val stringBody = Json.toJson(body).toString()
    WireMock.configureFor(wireMockServer.port())
    stubFor(
      get(urlEqualTo(url))
        .willReturn(aResponse()
          .withStatus(200)
          .withFixedDelay(getLatency)
          .withBody(stringBody))
    )
  }

  def mockSuccessNoContentGetRequest(url: String): Unit = {
    WireMock.configureFor(wireMockServer.port())
    stubFor(
      get(urlEqualTo(url))
        .willReturn(aResponse()
        .withFixedDelay(getLatency)
        .withStatus(204))
    )
  }

  def mockErrorGetRequest(url: String, body: String): Unit = {
    WireMock.configureFor(wireMockServer.port())
    stubFor(
      get(urlEqualTo(url))
        .willReturn(aResponse()
          .withStatus(500)
          .withFixedDelay(getLatency)
          .withBody(body))
    )
  }

}
