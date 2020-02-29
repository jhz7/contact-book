package co.com.addi.contact.book.infraestructure

import co.com.addi.contact.book.application.dtos.CriminalRecordDto

object PoliceRepublicDatabase {

  val data: Map[String, CriminalRecordDto] = Map(
    "1" -> CriminalRecordDto(firstName = "Jhon", lastName = "Zambrano", id = "1", typeId = "PA", descriptions = Nil),
    "5" -> CriminalRecordDto(firstName = "Carlos", lastName = "Olaya", id = "5", typeId = "CC", descriptions = List("Theft", "Violence")),
    "7" -> CriminalRecordDto(firstName = "Andr\u00E9s", lastName = "Carmona", id = "7", typeId = "PA", descriptions = List("Theft", "Violence"))
  )
}
