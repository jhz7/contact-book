package co.com.addi.contact.book.infraestructure.transformers

import co.com.addi.contact.book.application.dtos.PersonDto
import co.com.addi.contact.book.domain.models.{Dni, DniCode, Person}

object PersonTransformer {

  def toPerson(personDto: PersonDto): Person =
    Person(
      firstName = personDto.firstName,
      lastName = personDto.lastName,
      dni = Dni(
        number = personDto.id,
        code = DniCode(personDto.typeId)
      ),
      expeditionIdPlace = personDto.expeditionIdPlace
    )
}
