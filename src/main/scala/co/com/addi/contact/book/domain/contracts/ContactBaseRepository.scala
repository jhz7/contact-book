package co.com.addi.contact.book.domain.contracts

import akka.Done
import co.com.addi.contact.book.application.types.CustomEitherT
import co.com.addi.contact.book.domain.models.Contact

trait ContactBaseRepository {

  def save(person: Contact): CustomEitherT[Done]

}
