package co.com.addi.contactbook.application.services

import cats.data.EitherT
import co.com.addi.contactbook.domain.aliases.CustomEitherT
import co.com.addi.contactbook.domain.contracts.repositories.ProspectRepositoryContract
import co.com.addi.contactbook.domain.models.{Dni, Error, Prospect}
import co.com.addi.contactbook.domain.types.APPLICATION

case class ProspectPersistenceService(prospectRepository: ProspectRepositoryContract) {

  def get(dni: Dni): CustomEitherT[Prospect] = {
    prospectRepository.get(dni) flatMap{
      case Some(prospect) => EitherT.rightT(prospect)
      case None           => EitherT.leftT(Error(APPLICATION, s"The prospect ${dni.number} does not exist in temporary directory"))
    }
  }

}
