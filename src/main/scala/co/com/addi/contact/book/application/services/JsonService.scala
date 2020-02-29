package co.com.addi.contact.book.application.services

import co.com.addi.contact.book.application.dtos.{APPLICATION, ErrorDto}
import co.com.addi.contact.book.application.types.CustomEither
import play.api.libs.json.{Json, Reads}

trait JsonService {

  private val errorMessage = "The json value do not have an expected format..."

  def deserialize[T](value: String)(implicit reads: Reads[T]): CustomEither[T] = {
    Json.parse(value).validate[T].asEither match {
      case Right(pojo) => Right(pojo)
      case Left(_)     => Left(ErrorDto(APPLICATION, errorMessage))
    }
  }

}

object JsonService extends JsonService
