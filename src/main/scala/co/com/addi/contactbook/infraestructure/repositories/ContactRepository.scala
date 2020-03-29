package co.com.addi.contactbook.infraestructure.repositories

import akka.Done
import cats.data.EitherT
import co.com.addi.contactbook.domain.contracts.repositories.ContactRepositoryContract
import co.com.addi.contactbook.domain.models.{Contact, Error}
import monix.eval.Task

object ContactRepository extends ContactRepositoryContract {

  def save(person: Contact): CustomEitherT[Done] =
    EitherT.rightT[Task, Error](Done)

}
