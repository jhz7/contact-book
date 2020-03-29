package co.com.addi.contactbook.infraestructure.repositories

import cats.data.EitherT
import co.com.addi.contactbook.domain.contracts.repositories.ProspectRepositoryContract
import co.com.addi.contactbook.domain.models.{Contact, Dni, Error}
import co.com.addi.contactbook.infraestructure.databases.ProspectsDataBase
import co.com.addi.contactbook.infraestructure.transformers.PersonTransformer
import monix.eval.Task

object ProspectRepository extends ProspectRepositoryContract {

  override def get(dni: Dni): CustomEitherT[Option[Contact]] = {
    val person: Option[Contact] = ProspectsDataBase.data.get(dni.number).map(PersonTransformer.toPerson)

    EitherT.rightT[Task, Error](person)
  }

}
