package co.com.addi.contact.book.domain.services

import akka.Done
import co.com.addi.contact.book.application.dtos.{BUSINESS, CriminalRecordDto}
import co.com.addi.contact.book.application.types.CustomEither
import co.com.addi.contact.book.domain.models.{BUSINESS, Dni, Error}

trait ProspectCriminalRecordValidationService {

  def validateRecord(dni: Dni, criminalRecordDto: Option[CriminalRecordDto]): CustomEither[Done]

}

object ProspectCriminalRecordValidationService extends ProspectCriminalRecordValidationService {

  def validateRecord(dni: Dni, criminalRecordDto: Option[CriminalRecordDto]): CustomEither[Done] =
    if(criminalRecordDto.isEmpty || criminalRecordDto.exists(_.descriptions.isEmpty))
      Right(Done)
    else
      Left(Error(BUSINESS, s"The criminal record for the prospect ${dni.number} is not valid"))

}
