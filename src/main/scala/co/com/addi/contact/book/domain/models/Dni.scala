package co.com.addi.contact.book.domain.models

import co.com.addi.contact.book.domain.types.DniCode

final case class Dni(number: String, code: DniCode, expeditionPlace: String)
