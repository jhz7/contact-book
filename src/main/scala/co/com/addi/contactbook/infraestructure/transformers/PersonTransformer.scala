package co.com.addi.contactbook.infraestructure.transformers

import co.com.addi.contactbook.application.dtos.PersonDto
import co.com.addi.contactbook.domain.models.Contact

object PersonTransformer {

  def toPerson(personDto: PersonDto): Contact =
    Contact(
      firstName = personDto.firstName,
      lastName = personDto.lastName,
      dni = Dni(
        number = personDto.id,
        code = DniCode(personDto.typeId)
      ),
      expeditionIdPlace = personDto.expeditionIdPlace
    )
}
