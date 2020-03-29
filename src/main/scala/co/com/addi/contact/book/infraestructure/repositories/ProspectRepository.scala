package co.com.addi.contact.book.infraestructure.repositories

import cats.data.EitherT
import co.com.addi.contact.book.application.types.CustomEitherT
import co.com.addi.contact.book.domain.contracts.ProspectBaseRepository
import co.com.addi.contact.book.domain.models.{Contact, Dni, Error}
import co.com.addi.contact.book.infraestructure.databases.ProspectsDataBase
import co.com.addi.contact.book.infraestructure.transformers.PersonTransformer
import monix.eval.Task

object ProspectRepository extends ProspectBaseRepository {

  override def get(dni: Dni): CustomEitherT[Option[Contact]] = {
    val person: Option[Contact] = ProspectsDataBase.data.get(dni.number).map(PersonTransformer.toPerson)

    EitherT.rightT[Task, Error](person)
  }

}
