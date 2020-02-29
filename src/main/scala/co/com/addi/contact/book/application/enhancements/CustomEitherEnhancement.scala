package co.com.addi.contact.book.application.enhancements

import cats.data.EitherT
import co.com.addi.contact.book.application.types.{CustomEither, CustomEitherT}

object CustomEitherEnhancement {

  implicit class ConverToCustomEitherT[A](val either: CustomEither[A]) {

    def toCustomEitherT: CustomEitherT[A] =
      EitherT.fromEither(either)
  }
}
