package co.com.addi.contactbook.domain.enhancements

import cats.data.EitherT
import co.com.addi.contactbook.domain.aliases.{CustomEither, CustomEitherT}
import co.com.addi.contactbook.domain.models.Error

object CustomEitherEnhancement {

  implicit class ConvertToCustomEitherT[A](val either: CustomEither[A]) {
    def toCustomEitherT: CustomEitherT[A] = EitherT.fromEither(either)
  }

  implicit class TraverseEither[A](val values: List[CustomEither[A]]) {

    def group: CustomEither[List[A]] = {

      val leftProjections = values.filter(_.isLeft)
      if(leftProjections.isEmpty) return composeRight(values)

      composeLeft(leftProjections)
    }

    private def composeLeft( values: List[CustomEither[A]] ): CustomEither[List[A]] = {
      if(!values.forall(_.isLeft)) throw new IllegalArgumentException("All values must be left projections!")

      val errors = values.foldRight(Left(Nil): Either[List[Error], A]){
        (el, acc) => for {xs <- acc.left; x <- el.left} yield x :: xs
      }

      errors match {
        case Left(errors) => Left(Error.unifyErrors(errors))
        case Right( _ )   => throw new IllegalStateException()
      }
    }

    private def composeRight( values: List[CustomEither[A]] ): CustomEither[List[A]] =
      values.foldRight(Right(Nil): Either[Error, List[A]]){
        (el, acc) => for {xs <- acc; x <- el} yield x :: xs
      }
  }
}
