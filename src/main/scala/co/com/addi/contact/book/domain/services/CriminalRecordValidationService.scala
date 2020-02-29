package co.com.addi.contact.book.domain.services

import akka.Done
import co.com.addi.contact.book.application.dtos.{BUSINESS, CriminalRecordDto, ErrorDto}
import co.com.addi.contact.book.application.types.CustomEither
import co.com.addi.contact.book.domain.models.Dni

trait CriminalRecordValidationService {

  def validateRecord(dni: Dni, criminalRecordDto: Option[CriminalRecordDto]): CustomEither[Done]

}

object CriminalRecordValidationService extends CriminalRecordValidationService {

  def validateRecord(dni: Dni, criminalRecordDto: Option[CriminalRecordDto]): CustomEither[Done] =
    if(criminalRecordDto.isEmpty || criminalRecordDto.exists(_.descriptions.isEmpty))
      Right(Done)
    else
      Left(ErrorDto(BUSINESS, s"The criminal record for contact ${dni.number} is invalid"))

}
