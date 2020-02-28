package co.com.addi.contact.book.application.dtos

import play.api.libs.json.{Json, OFormat}

final case class CriminalRecordDto(
  firstName:    String,
  lastName:     String,
  id:           String,
  typeId:       String,
  descriptions: List[String]
)

object CriminalRecordDto{
  implicit  val formatCriminalRecordDto: OFormat[CriminalRecordDto] = Json.format[CriminalRecordDto]
}
