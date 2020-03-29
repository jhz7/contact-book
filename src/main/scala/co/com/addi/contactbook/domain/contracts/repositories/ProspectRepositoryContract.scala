package co.com.addi.contactbook.domain.contracts.repositories

import co.com.addi.contactbook.domain.aliases.CustomEitherT
import co.com.addi.contactbook.domain.models.{Contact, Dni}

trait ProspectRepositoryContract {

  def get(dni: Dni): CustomEitherT[Option[Contact]]

}
