package co.com.addi.contactbook.application.services

import akka.Done
import cats.data.EitherT
import co.com.addi.contactbook.application.commons.Logging
import co.com.addi.contactbook.domain.aliases._
import co.com.addi.contactbook.domain.contracts.repositories.{ContactRepositoryContract, ProspectRepositoryContract}
import co.com.addi.contactbook.domain.models.{Contact, Dni, Error, Prospect}
import co.com.addi.contactbook.domain.types.APPLICATION

case class ProspectProcessingService(
  prospectRepository: ProspectRepositoryContract,
  prospectRatingService: ProspectRatingServiceBase,
  contactRepository: ContactRepositoryContract,
  validationProspectVsRepublicSystemService: ValidationProspectVsRepublicSystemService
) {

  def validateProspect(dni: Dni): CustomEitherT[Done] = {
      Logging.info(s"Processing prospect with id ${dni.number}...", getClass)

      val processing: CustomEitherT[Done] =
        for {
          prospect <- getProspect(dni)
          _ <- validationProspectVsRepublicSystemService.validate(prospect)
          _ <- prospectRatingService.validateRating(prospect)
          contactToSave = Contact(prospect.firstName, prospect.lastName, prospect.dni)
          _ <- save(contactToSave)
        } yield Done

      processing.leftMap(error => { Logging.error(error.message, getClass); error })
  }

  private def getProspect(dni: Dni): CustomEitherT[Prospect] = {
      prospectRepository.get(dni) flatMap{
        case Some(prospect) => EitherT.rightT(prospect)
        case None           => EitherT.leftT(Error(APPLICATION, s"The prospect ${dni.number} does not exist in temporary directory"))
      }
  }

  private def save(contactToSave: Contact): CustomEitherT[Done] = {
      contactRepository.save(contactToSave).map( _ => {
        Logging.info(s"Contact ${contactToSave.dni.number} saved successfully!!", getClass)
        Done
      })
  }
}
