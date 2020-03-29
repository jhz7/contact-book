package co.com.addi.contactbook.domain.contracts.repositories

import akka.Done
import co.com.addi.contactbook.domain.aliases.CustomEitherT
import co.com.addi.contactbook.domain.models.Contact

trait ContactRepositoryContract {

  def save(person: Contact): CustomEitherT[Done]

}
