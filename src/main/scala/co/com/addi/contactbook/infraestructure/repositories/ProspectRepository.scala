package co.com.addi.contactbook.infraestructure.repositories

import cats.data.EitherT
import co.com.addi.contactbook.domain.aliases.CustomEitherT
import co.com.addi.contactbook.domain.contracts.repositories.ProspectRepositoryContract
import co.com.addi.contactbook.domain.models.{Contact, Dni, Error}
import co.com.addi.contactbook.infraestructure.datasets.ProspectsDataSet
import co.com.addi.contactbook.infraestructure.transformers.PersonTransformer
import monix.eval.Task

object ProspectRepository extends ProspectRepositoryContract {

  override def get(dni: Dni): CustomEitherT[Option[Contact]] =
    EitherT.rightT[Task, Error](ProspectsDataSet.data.get(dni.number).map(PersonTransformer.toPerson))

}
