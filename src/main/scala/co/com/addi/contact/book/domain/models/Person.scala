package co.com.addi.contact.book.domain.models

final case class Person(
  firstName:         String,
  lastName:          String,
  dni:               Dni,
  expeditionIdPlace: String
)
