package co.com.addi.contactbook.infraestructure.webserver

import co.com.addi.contactbook.application.commons.Logging
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
    Logging.info(s"Starting web server on $port port", getClass)
    wireMockServer.start()
  }

  def stopStubServer(): Unit = {
    Logging.info(s"Stopping web server on $port port", getClass)
    wireMockServer.stop()
  }

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

  def mockNoContentGetRequest(url: String): Unit = {
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
