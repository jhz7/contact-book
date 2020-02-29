package co.com.addi.contact.book.factories

import co.com.addi.contact.book.application.dtos.PersonDto
import co.com.addi.contact.book.domain.models.{Dni, IdentityCard, Person}

object PersonFactory {

  def createPersonDto: PersonDto =
    PersonDto(
      id = "",
      typeId = "",
      expeditionIdPlace = "",
      firstName = "",
      lastName = ""
    )

  def createPerson: Person =
    Person(
      firstName = "",
      lastName = "",
      dni = createDni,
      expeditionIdPlace = ""
    )

  def createDni: Dni = Dni(number = "", code = IdentityCard)
}
