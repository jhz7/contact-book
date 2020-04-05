package co.com.addi.contactbook.factories

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
