package co.com.addi.contact.book.application.dtos

final case class CriminalRecordDto(
  firstName:    String,
  lastName:     String,
  id:           String,
  typeId:       String,
  descriptions: List[String]
)
