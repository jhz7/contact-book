package co.com.addi.contact.book.application

import cats.data.{EitherT, ValidatedNel}
import co.com.addi.contact.book.domain.models.Error
import monix.eval.Task

package object types {

  type CustomEither[A] = Either[Error, A]

  type CustomEitherT[A] = EitherT[Task, Error, A]

  type CustomValidated[A] = ValidatedNel[Error, A]

}
