package co.com.addi.contactbook.application.services

import akka.Done
import cats.data.{EitherT, Reader}
import co.com.addi.contactbook.application.commons.Logging
import co.com.addi.contactbook.domain.aliases._
import co.com.addi.contactbook.domain.contracts.repositories.{ContactRepositoryContract, ProspectRepositoryContract}
import co.com.addi.contactbook.domain.contracts.wsclients.{RepublicIdentificationServiceContract, RepublicPoliceServiceContract}
import co.com.addi.contactbook.domain.enhancements.CustomEitherEnhancement._
import co.com.addi.contactbook.domain.models.{Contact, Dni, Error, Prospect}
import co.com.addi.contactbook.domain.types.{APPLICATION, BUSINESS}
import monix.eval.Task

trait ProspectProcessingServiceBase {

  def process(dni: Dni): Reader[(ProspectRepositoryContract, RepublicIdentificationServiceContract, RepublicPoliceServiceContract, ProspectRatingServiceBase, ContactRepositoryContract), CustomEitherT[Done]] = Reader {
    dependencies: (ProspectRepositoryContract, RepublicIdentificationServiceContract, RepublicPoliceServiceContract, ProspectRatingServiceBase, ContactRepositoryContract) =>
      Logging.info(s"Processing prospect with id ${dni.number}...", getClass)

      val processing: CustomEitherT[Done] =
        for {
          prospect <- getProspect(dni).run(dependencies._1)
          _ <- validateProspectVsRepublicSystem(prospect).run((dependencies._2, dependencies._3))
          _ <- validateProspectRating(prospect).run(dependencies._4)
          _ <- save(prospect).run(dependencies._5)
        } yield Done

      processing.leftMap(error => {
        Logging.error(error.message, getClass)
        error
      })
  }

  private def getProspect(dni: Dni): Reader[ProspectRepositoryContract, CustomEitherT[Prospect]] = Reader {
    prospectRepository: ProspectRepositoryContract =>

      prospectRepository.get(dni) flatMap{
        case Some(prospect) => EitherT.rightT(prospect)
        case None           => EitherT.leftT(Error(APPLICATION, s"The prospect ${dni.number} does not exist in temporary directory"))
      }
  }

  private def validateProspectVsRepublicSystem(prospect: Prospect): Reader[(RepublicIdentificationServiceContract, RepublicPoliceServiceContract), CustomEitherT[Done]] = Reader {
    dependencies: (RepublicIdentificationServiceContract, RepublicPoliceServiceContract) =>

      val validationData = validateProspectData(prospect).run(dependencies._1).value
      val validationCriminalRecord = validateProspectCriminalRecord(prospect).run(dependencies._2).value

      val execution: Task[CustomEither[List[Done]]] =
        Task.gatherUnordered( List(validationData, validationCriminalRecord) ).map(_.group)

      EitherT(execution)map(_ => Done)
  }

  private def validateProspectData(prospect: Prospect): Reader[RepublicIdentificationServiceContract, CustomEitherT[Done]] = Reader {
    republicIdentificationService: RepublicIdentificationServiceContract =>

      Logging.info(s"Validating personal data for prospect ${prospect.dni.number}...", getClass)

      republicIdentificationService
        .getProspectData(prospect.dni)
        .subflatMap{
          case Some(prospectData) => prospect.validateEqualityData(prospectData)
          case None               => Left(Error(APPLICATION, s"The prospect ${prospect.dni.number} does not exist in republic identification system"))
        }
  }

  private def validateProspectCriminalRecord(prospect: Prospect): Reader[RepublicPoliceServiceContract, CustomEitherT[Done]] = Reader {
    republicPoliceService: RepublicPoliceServiceContract =>

      Logging.info(s"Validating criminal record for prospect ${prospect.dni.number}...", getClass)

      republicPoliceService.existsCriminalRecord(prospect.dni)
        .subflatMap{
          case true  => Left(Error(BUSINESS, s"Exists criminal record for the prospect ${prospect.dni.number}"))
          case false => Right(Done)
        }
  }

  private def validateProspectRating(prospect: Prospect): Reader[ProspectRatingServiceBase, CustomEitherT[Done]] = Reader {
    prospectRatingService: ProspectRatingServiceBase =>

      Logging.info(s"Validating rating for prospect ${prospect.dni.number}...", getClass)

      val score = prospectRatingService.rate(prospect.dni)
      prospect.validateScore(score).toCustomEitherT
  }

  private def save(prospect: Prospect): Reader[ContactRepositoryContract, CustomEitherT[Done]] = Reader {
    contactRepository: ContactRepositoryContract =>

      val contactToSave = Contact(prospect.firstName, prospect.lastName, prospect.dni)

      contactRepository.save(contactToSave).map( _ => {
        Logging.info(s"Contact ${contactToSave.dni.number} saved successfully!!", getClass)
        Done
      })
  }

}

object ProspectProcessingService extends ProspectProcessingServiceBase
