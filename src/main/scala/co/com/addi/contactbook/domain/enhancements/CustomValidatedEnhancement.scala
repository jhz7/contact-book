package co.com.addi.contactbook.domain.enhancements

import cats.data.Validated.{Invalid, Valid}
import co.com.addi.contactbook.domain.aliases._
import co.com.addi.contactbook.domain.models
import co.com.addi.contactbook.domain.models.Error
import co.com.addi.contactbook.domain.types.{APPLICATION, BUSINESS, TECHNICAL}

object CustomValidatedEnhancement {

  implicit class FromValidatedNelToEither[A](val validated: CustomValidated[A]) {

    def toCustomEither: CustomEither[A] =
      validated match {
        case Valid(value)    => Right(value)
        case Invalid(errors) => Left(unifyErrors(errors.toList))
      }

    private def unifyErrors(errors: List[models.Error]): models.Error = {

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
