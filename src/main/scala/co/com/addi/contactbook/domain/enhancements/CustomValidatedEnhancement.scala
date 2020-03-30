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
        case Invalid(errors) => Left(Error.unifyErrors(errors.toList))
      }
  }

}
