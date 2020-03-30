package co.com.addi.contactbook.domain.contracts.wsclients

import co.com.addi.contactbook.domain.aliases.CustomEitherT
import co.com.addi.contactbook.domain.models.{Dni, Prospect}

trait RepublicIdentificationServiceContract {
  def getProspectData(dni: Dni): CustomEitherT[Option[Prospect]]
}
