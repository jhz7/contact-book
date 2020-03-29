package co.com.addi.contactbook.infraestructure.datasets

import co.com.addi.contactbook.application.dtos.CriminalRecordDto

object RepublicPoliceDataSet {

  val data: Map[String, CriminalRecordDto] = Map(
    "5" -> CriminalRecordDto(id = "5", typeId = "CC", descriptions = "Theft"),
    "7" -> CriminalRecordDto(id = "7", typeId = "PA", descriptions = "Violence")
  )
}
