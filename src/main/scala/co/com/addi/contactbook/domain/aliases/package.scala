package co.com.addi.contactbook.domain

import cats.data.{EitherT, ValidatedNel}
import monix.eval.Task

package object aliases {

  type CustomEither[A] = Either[models.Error, A]

  type CustomEitherT[A] = EitherT[Task, models.Error, A]

  type CustomValidated[A] = ValidatedNel[models.Error, A]

}
