package co.com.addi.contactbook.domain.models

import co.com.addi.contactbook.domain.types.DniCode

final case class Dni(number: String, code: DniCode, expeditionPlace: String)
