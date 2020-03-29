package co.com.addi.contactbook.domain.contracts.wsclients

import co.com.addi.contactbook.domain.aliases.CustomEitherT
import co.com.addi.contactbook.domain.models.Dni

trait RepublicPoliceServiceContract {
  def existsCriminalRecord(dni: Dni): CustomEitherT[Boolean]
}
