package co.com.addi.contact.book.infraestructure.repositories

import akka.Done
import cats.data.EitherT
import co.com.addi.contact.book.application.types.CustomEitherT
import co.com.addi.contact.book.domain.contracts.ContactBaseRepository
import co.com.addi.contact.book.domain.models.{Contact, Error}
import monix.eval.Task

object ContactRepository extends ContactBaseRepository {

  def save(person: Contact): CustomEitherT[Done] =
    EitherT.rightT[Task, Error](Done)

}
