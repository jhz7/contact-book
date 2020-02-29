package co.com.addi.contact.book.domain.contracts

import co.com.addi.contact.book.application.types.CustomEitherT
import co.com.addi.contact.book.domain.models.{Dni, Person}

trait ProspectBaseRepository {

  def get(dni: Dni): CustomEitherT[Option[Person]]

}
