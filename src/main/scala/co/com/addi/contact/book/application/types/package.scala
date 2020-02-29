package co.com.addi.contact.book.application

import cats.data.{EitherT, ValidatedNel}
import co.com.addi.contact.book.application.dtos.ErrorDto
import monix.eval.Task

package object types {

  type CustomEither[A] = Either[ErrorDto, A]

  type CustomEitherT[A] = EitherT[Task, ErrorDto, A]

  type CustomValidated[A] = ValidatedNel[ErrorDto, A]

}
