package co.com.addi.contactbook.application.services

import akka.Done
import co.com.addi.contactbook.application.commons.Logging
import co.com.addi.contactbook.domain.aliases.CustomEitherT
import co.com.addi.contactbook.domain.models.{Contact, Dni}

case class ProspectProcessingService(
  prospectScoringValidationService: ProspectScoringValidationService,
  prospectDataValidationService:    ProspectDataValidationService,
  prospectPersistenceService:       ProspectPersistenceService,
  contactPersistenceService:        ContactPersistenceService
) {

  def becomeContact(dni: Dni): CustomEitherT[Done] = {
    Logging.info(s"Processing prospect with id ${dni.number}...", getClass)

    val processing: CustomEitherT[Done] =
      for {
        prospect <- prospectPersistenceService.get(dni)
        _ <- prospectDataValidationService.validate(prospect)
        _ <- prospectScoringValidationService.validate(prospect)
        contactToSave = Contact(prospect.firstName, prospect.lastName, prospect.dni)
        _ <- contactPersistenceService.save(contactToSave)
      } yield Done

    processing.leftMap(error => { Logging.error(error.message, getClass); error })
  }
}
