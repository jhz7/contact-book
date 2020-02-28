package co.com.addi.contact.book.infraestructure.wsclients

import co.com.addi.contact.book.application.dtos.PersonDto
import co.com.addi.contact.book.application.types.CustomEitherT
import co.com.addi.contact.book.domain.models.Dni

trait IdRepublicService {

  def getPerson(dni: Dni): CustomEitherT[Option[PersonDto]]

}
