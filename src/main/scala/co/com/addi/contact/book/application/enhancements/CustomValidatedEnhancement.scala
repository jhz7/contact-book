package co.com.addi.contact.book.application.enhancements

import cats.data.Validated.{Invalid, Valid}
import co.com.addi.contact.book.application.dtos.{APPLICATION, BUSINESS, ErrorDto, TECHNICAL}
import co.com.addi.contact.book.application.types.{CustomEither, CustomValidated}

object CustomValidatedEnhancement {

  implicit class FromValidatedNelToEither[A](val validated: CustomValidated[A]) {

    def toCustomEither: CustomEither[A] =
      validated match {
        case Valid(value)    => Right(value)
        case Invalid(errors) => Left(unifyErrors(errors.toList))
      }

    private def unifyErrors(errors: List[ErrorDto]): ErrorDto = {

      val typeErrors = errors.map(_.`type`)
      val unifiedMessage = errors.map(_.message).mkString(". ")

      (typeErrors.contains(BUSINESS), typeErrors.contains(APPLICATION), typeErrors.contains(TECHNICAL)) match {
        case (true, _, _) => ErrorDto(BUSINESS, unifiedMessage)
        case (_, true, _) => ErrorDto(APPLICATION, unifiedMessage)
        case (_, _, true) => ErrorDto(TECHNICAL, unifiedMessage)
        case _            => ErrorDto(APPLICATION, unifiedMessage)
      }
    }
  }

}
