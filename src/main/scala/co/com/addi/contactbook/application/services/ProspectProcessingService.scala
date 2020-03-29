package co.com.addi.contactbook.application.services

import akka.Done
import cats.data.{EitherT, Reader}
import cats.implicits._
import co.com.addi.contactbook.application.commons.Logging
import co.com.addi.contactbook.domain.models.{Contact, Dni, Error}
import co.com.addi.contactbook.infraestructure.ServiceLocator
import monix.eval.Task

trait ProspectProcessingServiceBase {

  def process(dni: Dni): Reader[ServiceLocator, CustomEitherT[Done]] = Reader {
    dependencies: ServiceLocator =>
      Logging.info(s"Processing prospect with id ${dni.number}...", getClass)

      val processing: CustomEitherT[Done] =
        for {
          prospect <- getProspect(dni).run(dependencies)
          _ <- validateProspectInformationWithRepublicSystem(prospect).run(dependencies)
          _ <- validateProspectRating(prospect).run(dependencies)
          _ <- save(prospect).run(dependencies)
        } yield Done

      processing.leftMap(error => {
        Logging.error(error.message, getClass)
        error
      })
  }

  private def getProspect(dni: Dni): Reader[ServiceLocator, CustomEitherT[Contact]] = Reader {
    dependencies: ServiceLocator =>
      dependencies.prospectRepository.get(dni) flatMap{
        case Some(person) => EitherT.rightT[Task, Error](person)
        case None         => EitherT.leftT(Error(APPLICATION, s"The prospect ${dni.number} does not exist in temporary directory"))
      }
  }

  private def validateProspectInformationWithRepublicSystem(prospect: Contact): Reader[ServiceLocator, CustomEitherT[Done]] = Reader {
    dependencies: ServiceLocator =>

      val validationData = validateProspectData(prospect).run(dependencies).value
      val validationCriminalRecord = validateProspectCriminalRecord(prospect).run(dependencies).value

      val validationExecution: Task[CustomEither[Done]] =
        for {
          validationDataResult <- validationData
          validationCriminalRecordResult <- validationCriminalRecord
        } yield
          (validationDataResult, validationCriminalRecordResult).mapN((_, _) => Done)

      EitherT(validationExecution)
  }

  private def validateProspectData(prospect: Contact): Reader[ServiceLocator, CustomEitherT[Done]] = Reader {
    dependencies: ServiceLocator =>
      Logging.info(s"Validating personal data for prospect ${prospect.dni.number}...", getClass)

      dependencies.republicIdentificationService
        .getPerson(prospect.dni).run(dependencies.wsClient)
        .subflatMap{
          case Some(person) => dependencies.prospectDataValidationService.validateData(prospect, person)
          case None         => Left(Error(APPLICATION, s"The prospect ${prospect.dni.number} does not exist in republic identification system"))
        }
  }

  private def validateProspectCriminalRecord(prospect: Contact): Reader[ServiceLocator, CustomEitherT[Done]] = Reader {
    dependencies: ServiceLocator =>
      Logging.info(s"Validating criminal record for prospect ${prospect.dni.number}...", getClass)

      dependencies.republicPoliceService
        .getCriminalRecord(prospect.dni).run(dependencies.wsClient)
        .subflatMap(possibleCriminalRecord =>
          dependencies.prospectCriminalRecordValidationService.validateRecord(prospect.dni, possibleCriminalRecord)
        )
  }

  private def validateProspectRating(prospect: Contact): Reader[ServiceLocator, CustomEitherT[Done]] = Reader {
    dependencies: ServiceLocator =>
      Logging.info(s"Validating rating for prospect ${prospect.dni.number}...", getClass)

      val score = dependencies.prospectRatingService.rate(prospect.dni)

      dependencies.prospectScoreValidationService
        .validateScore(prospect.dni, score).toCustomEitherT
  }

  private def save(prospect: Contact): Reader[ServiceLocator, CustomEitherT[Done]] = Reader {
    dependencies: ServiceLocator =>

      dependencies.contactRepository.save(prospect).map( _ => {
        Logging.info(s"Contact ${prospect.dni.number} saved successfully!!", getClass)
        Done
      })
  }

}

object ProspectProcessingService extends ProspectProcessingServiceBase
