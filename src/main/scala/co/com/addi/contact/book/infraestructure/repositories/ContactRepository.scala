package co.com.addi.contact.book.infraestructure.repositories

import akka.Done
import cats.data.EitherT
import co.com.addi.contact.book.application.dtos.ErrorDto
import co.com.addi.contact.book.application.types.CustomEitherT
import co.com.addi.contact.book.domain.contracts.ContactBaseRepository
import co.com.addi.contact.book.domain.models.Person
import monix.eval.Task

object ContactRepository extends ContactBaseRepository {

  def save(person: Person): CustomEitherT[Done] =
    EitherT.rightT[Task, ErrorDto](Done)

}
