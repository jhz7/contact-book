package co.com.addi.contact.book.domain.contracts

import akka.Done
import co.com.addi.contact.book.application.types.CustomEitherT
import co.com.addi.contact.book.domain.models.Person

trait ContactBaseRepository {

  def save(person: Person): CustomEitherT[Done]

}
