package co.com.addi.contactbook.domain.enhancements

import cats.data.EitherT
import co.com.addi.contactbook.domain.aliases.{CustomEither, CustomEitherT}
import co.com.addi.contactbook.domain.models.Error

object CustomEitherEnhancement {

  implicit class ConverToCustomEitherT[A](val either: CustomEither[A]) {

    def toCustomEitherT: CustomEitherT[A] =
      EitherT.fromEither(either)
  }

  implicit class TraverseEither[A](val values: List[CustomEither[A]]) {

    def group: CustomEither[List[A]] = {

      val leftProjections = values.filter(_.isLeft)
      val rightProjections = values.filter(_.isRight)

      if(leftProjections.isEmpty)
        return composeRight(rightProjections)

      composeLeft(leftProjections)
    }

    private def composeLeft( values: List[CustomEither[A]]): CustomEither[List[A]] = {
      values.foldRight(Left(Nil): Either[List[Error], A]){
        (el, acc) => for {xs <- acc.left; x <- el.left} yield x :: xs
      }
        .fold(
          errors => Left(Error.unifyErrors(errors)),
          _ => Right(Nil)
        )
    }

    private def composeRight( values: List[CustomEither[A]]): CustomEither[List[A]] =
      values.foldRight(Right(Nil): Either[Error, List[A]]){
        (el, acc) => for {xs <- acc; x <- el} yield x :: xs
      }
  }
}
