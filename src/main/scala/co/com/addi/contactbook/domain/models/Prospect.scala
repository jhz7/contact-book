package co.com.addi.contactbook.domain.models

import akka.Done
import cats.implicits._
import co.com.addi.contactbook.domain.aliases._
import co.com.addi.contactbook.domain.enhancements.CustomValidatedEnhancement._
import co.com.addi.contactbook.domain.types.BUSINESS

class Prospect( val firstName: String, val lastName: String, val dni: Dni ) {

  def validateScore(minimumScore: Int, currentScore: Int): CustomEither[Done] = {
    if(currentScore < minimumScore)
      return Left(Error(BUSINESS, s"The score for the prospect ${this.dni.number} is below the minimum allowed"))

    return Right(Done)
  }

  def validateEqualityData(that: Prospect): CustomEither[Done] =
    (
      validateItem(that.dni.number, this.dni.number, "DNI Number"),
      validateItem(that.dni.code, this.dni.code, "DNI Code"),
      validateItem(that.firstName, this.firstName, "First Name"),
      validateItem(that.lastName, this.lastName, "Last Name")
    )
      .mapN((_, _, _, _) => Done)
      .toCustomEither


  private def validateItem[T](thisField: T, thatField: T, fieldName: String): CustomValidated[Done] = {
    if(!(thisField == thatField))
      return Error(BUSINESS, s"The $fieldName value is not valid for prospect ${this.dni.code.description}: ${this.dni.number}").invalidNel

    return Done.valid
  }

}
