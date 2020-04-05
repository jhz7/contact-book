package co.com.addi.contactbook.factories

import co.com.addi.contactbook.application.dtos.PersonDto
import co.com.addi.contactbook.domain.models.{Contact, Dni}
import co.com.addi.contactbook.domain.types.IdentityCard

object PersonFactory {

  def createPersonDto: PersonDto =
    PersonDto(
      id = "",
      typeId = "",
      expeditionIdPlace = "",
      firstName = "",
      lastName = ""
    )

  def createContact: Contact =
    Contact(
      firstName = "",
      lastName = "",
      dni = createDni
    )

  def createDni: Dni = Dni(number = "", code = IdentityCard, expeditionPlace = "")
}
