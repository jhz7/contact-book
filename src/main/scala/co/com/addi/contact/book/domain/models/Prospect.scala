package co.com.addi.contact.book.domain.models

import akka.Done
import cats.implicits._
import co.com.addi.contact.book.application.types.{CustomEither, CustomValidated}
import co.com.addi.contact.book.domain.types.BUSINESS
import co.com.addi.contact.book.application.enhancements.CustomValidatedEnhancement._

class Prospect(
  val firstName: String,
  val lastName:  String,
  val dni:       Dni
) {

  def isEqual(that: Prospect): CustomEither[Done] =
    (
      validateItem(that.dni.number, this.dni.number, "DNI Number"),
      validateItem(that.dni.code, this.dni.code, "DNI Code"),
      validateItem(that.firstName, this.firstName, "First Name"),
      validateItem(that.lastName, this.lastName, "Last Name")
    )
      .mapN((_, _, _, _) => Done)
      .toCustomEither


  private def validateItem[T](thisField: T, thatField: T, fieldName: String): CustomValidated[Done] =
    if(thisField == thatField)
      Done.valid
    else
      Error(BUSINESS, s"The $fieldName value is not valid for prospect ${this.dni.code.description}: ${this.dni.number}").invalidNel

}
