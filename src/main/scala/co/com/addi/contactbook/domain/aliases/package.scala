package co.com.addi.contactbook.domain

import cats.data.{EitherT, ValidatedNel}
import co.com.addi.contactbook.domain.models.Error
import monix.eval.Task

package object aliases {

  type CustomEither[A] = Either[Error, A]

  type CustomEitherT[A] = EitherT[Task, Error, A]

  type CustomValidated[A] = ValidatedNel[Error, A]

}
