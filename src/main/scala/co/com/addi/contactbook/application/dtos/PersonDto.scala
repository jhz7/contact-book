package co.com.addi.contactbook.application.dtos

import play.api.libs.json.{Json, OFormat}

final case class PersonDto(
  id:                String,
  typeId:            String,
  expeditionIdPlace: String,
  firstName:         String,
  lastName:          String
)

object PersonDto{
  implicit val formatPersonDto: OFormat[PersonDto] = Json.format[PersonDto]
}
