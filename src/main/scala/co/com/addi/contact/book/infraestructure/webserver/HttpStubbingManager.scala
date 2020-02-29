package co.com.addi.contact.book.infraestructure.webserver

import co.com.addi.contact.book.infraestructure.logger.Logger
import play.api.libs.json.Writes

import scala.util.Random

trait HttpStubbingManager {

  val webServerErrorMessage: String

  sealed trait KindHttpResponses
  object SUCCESS extends KindHttpResponses
  object SUCCESS_NO_CONTENT extends KindHttpResponses
  object ERROR extends KindHttpResponses

  private val process: Map[Int, KindHttpResponses] = Map(
    0 -> SUCCESS,
    1 -> SUCCESS_NO_CONTENT,
    2 -> ERROR
  )

  def stubbingRandomlyWebServer[T](url: String, successResponse: T)(implicit writes: Writes[T]): Unit = {
    val processNumber = Random.nextInt(3)

    process.get(processNumber) match {
      case Some(ERROR)              => WebServerStub.mockErrorGetRequest(url, webServerErrorMessage)
      case Some(SUCCESS)            => WebServerStub.mockSuccessGetRequest(url, successResponse)
      case Some(SUCCESS_NO_CONTENT) => WebServerStub.mockSuccessNoContentGetRequest(url)
      case _ =>
        Logger.error("This must not happen...", None, getClass)
    }
  }
}
