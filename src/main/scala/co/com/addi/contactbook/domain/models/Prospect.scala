package co.com.addi.contactbook.domain.models

import akka.Done
import cats.implicits._
import co.com.addi.contactbook.domain.aliases._
import co.com.addi.contactbook.domain.enhancements.CustomValidatedEnhancement._
import co.com.addi.contactbook.domain.types.BUSINESS

case class Prospect(firstName: String, lastName: String, dni: Dni) {

  private val minimumAllowedScoreToBecomeAcontact = 60

  def validateScore(currentlyObtainedScore: Int): CustomEither[Done] = {
    if(currentlyObtainedScore < minimumAllowedScoreToBecomeAcontact)
      return Left(Error(BUSINESS, s"The score for the prospect ${this.dni.number} is below the minimum allowed"))

    Right(Done)
  }

  def validateDataEquality(that: Prospect): CustomEither[Done] =
    (
      validateItem(that.dni.number, this.dni.number, "DNI Number"),
      validateItem(that.dni.code, this.dni.code, "DNI Type"),
      validateItem(that.firstName, this.firstName, "First Name"),
      validateItem(that.lastName, this.lastName, "Last Name")
    )
      .mapN((_, _, _, _) => Done)
      .toCustomEither


  private def validateItem[T](thisField: T, thatField: T, fieldName: String): CustomValidated[Done] = {
    if(!(thisField == thatField))
      return Error(BUSINESS, s"The '$fieldName' value is not valid for prospect ${this.dni.number}").invalidNel

    Done.valid
  }

}
