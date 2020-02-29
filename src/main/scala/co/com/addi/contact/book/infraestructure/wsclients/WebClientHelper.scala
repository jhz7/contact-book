package co.com.addi.contact.book.infraestructure.wsclients

import co.com.addi.contact.book.application.dtos.{APPLICATION, ErrorDto}
import co.com.addi.contact.book.application.types.CustomEither
import play.api.libs.json.{Json, Reads}
import play.api.libs.ws.ahc.StandaloneAhcWSRequest

trait WebClientHelper {

  def processWebResponse[T](webResponse: StandaloneAhcWSRequest#Response)(implicit reads: Reads[T]): CustomEither[Option[T]] ={
    if(webResponse.status == 200)
      deserialize[T](webResponse.body).map(Some(_))
    else if(webResponse.status == 204)
      Right(None)
    else
      Left(ErrorDto(APPLICATION, webResponse.body))
  }

  private def deserialize[T](value: String)(implicit reads: Reads[T]): CustomEither[T] = {
    Json.parse(value).validate[T].asEither match {
      case Right(pojo) => Right(pojo)
      case Left(_)     => Left(ErrorDto(APPLICATION, "The json value do not have an expected format..."))
    }
  }

}
