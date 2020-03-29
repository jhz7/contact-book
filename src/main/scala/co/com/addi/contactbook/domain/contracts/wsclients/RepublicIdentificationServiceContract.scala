package co.com.addi.contactbook.domain.contracts.wsclients

import co.com.addi.contactbook.domain.aliases.CustomEitherT
import co.com.addi.contactbook.domain.models.{Contact, Dni}

trait RepublicIdentificationServiceContract {
  def getPerson(dni: Dni): CustomEitherT[Option[Contact]]
}
