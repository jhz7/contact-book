package co.com.addi.contact.book.infraestructure.wsclients

import co.com.addi.contact.book.application.dtos.CriminalRecordDto
import co.com.addi.contact.book.application.types.CustomEitherT
import co.com.addi.contact.book.domain.models.Dni

trait PoliceRepublicService {

  def getCriminalRecord(dni: Dni): CustomEitherT[Option[CriminalRecordDto]]
}

object PoliceRepublicService extends PoliceRepublicService {

  def getCriminalRecord( dni: Dni ): CustomEitherT[Option[CriminalRecordDto]] = {
    ???
  }
}
