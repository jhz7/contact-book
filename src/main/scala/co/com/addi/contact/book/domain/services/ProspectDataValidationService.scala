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

  private val invalidIdMessage = "The id value is not valid"
  private val invalidTypeIdMessage = "The type id value is not valid"
  private val invalidFirstNameMessage = "The first name value is not valid"
  private val invalidLastNameMessage = "The last name value is not valid"

  def validateData(prospect: Person, dataFromIdentificationService: Person): CustomEither[Done] =
    (
      validateItem(dataFromIdentificationService.dni.number, prospect.dni.number, invalidIdMessage),
      validateItem(dataFromIdentificationService.dni.code, prospect.dni.number, invalidTypeIdMessage),
      validateItem(dataFromIdentificationService.firstName, prospect.firstName, invalidFirstNameMessage),
      validateItem(dataFromIdentificationService.lastName, prospect.lastName, invalidLastNameMessage)
    ).mapN((_, _, _, _) => Done)
      .toCustomEither


  private def validateItem[T](itemFromIdentificationService: T, itemToCompare: T, errorMessage: String): CustomValidated[Done] =
    if(itemFromIdentificationService == itemToCompare) Done.valid
    else ErrorDto(BUSINESS, errorMessage).invalidNel
}
