package co.com.addi.contact.book.factories

import co.com.addi.contact.book.application.dtos.CriminalRecordDto

object CriminalRecordFactory {

  def getInstance: CriminalRecordDto =
    CriminalRecordDto(
      firstName = "",
      lastName = "",
      id = "",
      typeId = "",
      descriptions = Nil
    )
}
