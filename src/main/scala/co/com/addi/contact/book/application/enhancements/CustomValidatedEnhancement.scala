package co.com.addi.contact.book.application.enhancements

import cats.data.Validated.{Invalid, Valid}
import co.com.addi.contact.book.application.types.{CustomEither, CustomValidated}
import co.com.addi.contact.book.domain.models.Error
import co.com.addi.contact.book.domain.types.{APPLICATION, BUSINESS, TECHNICAL}

object CustomValidatedEnhancement {

  implicit class FromValidatedNelToEither[A](val validated: CustomValidated[A]) {

    def toCustomEither: CustomEither[A] =
      validated match {
        case Valid(value)    => Right(value)
        case Invalid(errors) => Left(unifyErrors(errors.toList))
      }

    private def unifyErrors(errors: List[Error]): Error = {

      val typeErrors = errors.map(_.code)
      val unifiedMessage = errors.map(_.message).mkString(". ")

      (typeErrors.contains(BUSINESS), typeErrors.contains(APPLICATION), typeErrors.contains(TECHNICAL)) match {
        case (true, _, _) => Error(BUSINESS, unifiedMessage)
        case (_, true, _) => Error(APPLICATION, unifiedMessage)
        case (_, _, true) => Error(TECHNICAL, unifiedMessage)
        case _            => Error(APPLICATION, unifiedMessage)
      }
    }
  }

}
