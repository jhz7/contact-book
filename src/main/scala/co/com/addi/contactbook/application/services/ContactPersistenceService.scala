package co.com.addi.contactbook.application.services

import akka.Done
import co.com.addi.contactbook.application.commons.Logging
import co.com.addi.contactbook.domain.aliases.CustomEitherT
import co.com.addi.contactbook.domain.contracts.repositories.ContactRepositoryContract
import co.com.addi.contactbook.domain.models.Contact

case class ContactPersistenceService(contactRepository: ContactRepositoryContract) {

  def save(contactToSave: Contact): CustomEitherT[Done] = {
    contactRepository.save(contactToSave).map( _ => {
      Logging.info(s"Contact ${contactToSave.dni.number} saved successfully!!", getClass)
      Done
    })
  }
}
