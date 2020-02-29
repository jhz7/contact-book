package co.com.addi.contact.book.domain.services

import akka.Done
import cats.implicits._
import co.com.addi.contact.book.application.dtos.{BUSINESS, ErrorDto}
import co.com.addi.contact.book.application.enhancements.CustomValidatedEnhancement._
import co.com.addi.contact.book.application.types.{CustomEither, CustomValidated}
import co.com.addi.contact.book.domain.models.Person

trait ProspectDataValidationService {

  def validateData(prospect: Person, dataFromIdentificationService: Person): CustomEither[Done]

}

object ProspectDataValidationService extends ProspectDataValidationService {

  private val invalidIdMessage: String => String =
    id => s"The id value is not valid for prospect $id"
  private val invalidTypeIdMessage: String => String =
    id => s"The id type value is not valid for prospect $id"
  private val invalidFirstNameMessage: String => String =
    id => s"The first name value is not valid for prospect $id"
  private val invalidLastNameMessage: String => String =
    id => s"The last name value is not valid for prospect $id"

  def validateData(prospect: Person, dataFromIdentificationService: Person): CustomEither[Done] =
    (
      validateItem(dataFromIdentificationService.dni.number, prospect.dni.number, invalidIdMessage(prospect.dni.number)),
      validateItem(dataFromIdentificationService.dni.code, prospect.dni.code, invalidTypeIdMessage(prospect.dni.number)),
      validateItem(dataFromIdentificationService.firstName, prospect.firstName, invalidFirstNameMessage(prospect.dni.number)),
      validateItem(dataFromIdentificationService.lastName, prospect.lastName, invalidLastNameMessage(prospect.dni.number))
    ).mapN((_, _, _, _) => Done)
      .toCustomEither


  private def validateItem[T](itemFromIdentificationService: T, itemToCompare: T, errorMessage: String): CustomValidated[Done] =
    if(itemFromIdentificationService == itemToCompare) Done.valid
    else ErrorDto(BUSINESS, errorMessage).invalidNel
}
