package co.com.addi.contactbook.application.dtos

import play.api.libs.json.{Json, OFormat}

final case class CriminalRecordDto(
  id:           String,
  typeId:       String,
  descriptions: String
)

object CriminalRecordDto{
  implicit  val formatCriminalRecordDto: OFormat[CriminalRecordDto] = Json.format[CriminalRecordDto]
}
