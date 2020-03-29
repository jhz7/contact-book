package co.com.addi.contactbook.domain.enhancements

import cats.data.EitherT
import co.com.addi.contactbook.domain.aliases.{CustomEither, CustomEitherT}

object CustomEitherEnhancement {

  implicit class ConverToCustomEitherT[A](val either: CustomEither[A]) {

    def toCustomEitherT: CustomEitherT[A] =
      EitherT.fromEither(either)
  }
}
