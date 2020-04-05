package co.com.addi.contactbook.factories

import co.com.addi.contactbook.application.dtos.CriminalRecordDto

object CriminalRecordFactory {

  def getInstance: CriminalRecordDto =
    CriminalRecordDto(
      id = "",
      typeId = "",
      descriptions = ""
    )
}
