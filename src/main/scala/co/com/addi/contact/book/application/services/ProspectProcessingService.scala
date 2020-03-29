package co.com.addi.contact.book.application.services

import akka.Done
import cats.data.{EitherT, Reader}
import cats.implicits._
import co.com.addi.contact.book.application.Dependencies
import co.com.addi.contact.book.application.commons.Logging
import co.com.addi.contact.book.application.dtos.APPLICATION
import co.com.addi.contact.book.application.enhancements.CustomEitherEnhancement._
import co.com.addi.contact.book.application.types.{CustomEither, CustomEitherT}
import co.com.addi.contact.book.domain.models.{APPLICATION, Contact, Dni, Error}
import monix.eval.Task

trait ProspectProcessingService {

  def process(dni: Dni): Reader[Dependencies, CustomEitherT[Done]] = Reader {
    dependencies: Dependencies =>
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

  private def getProspect(dni: Dni): Reader[Dependencies, CustomEitherT[Contact]] = Reader {
    dependencies: Dependencies =>
      dependencies.prospectRepository.get(dni) flatMap{
        case Some(person) => EitherT.rightT[Task, Error](person)
        case None         => EitherT.leftT(Error(APPLICATION, s"The prospect ${dni.number} does not exist in temporary directory"))
      }
  }

  private def validateProspectInformationWithRepublicSystem(prospect: Contact): Reader[Dependencies, CustomEitherT[Done]] = Reader {
    dependencies: Dependencies =>

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

  private def validateProspectData(prospect: Contact): Reader[Dependencies, CustomEitherT[Done]] = Reader {
    dependencies: Dependencies =>
      Logging.info(s"Validating personal data for prospect ${prospect.dni.number}...", getClass)

      dependencies.republicIdentificationService
        .getPerson(prospect.dni).run(dependencies.wsClient)
        .subflatMap{
          case Some(person) => dependencies.prospectDataValidationService.validateData(prospect, person)
          case None         => Left(Error(APPLICATION, s"The prospect ${prospect.dni.number} does not exist in republic identification system"))
        }
  }

  private def validateProspectCriminalRecord(prospect: Contact): Reader[Dependencies, CustomEitherT[Done]] = Reader {
    dependencies: Dependencies =>
      Logging.info(s"Validating criminal record for prospect ${prospect.dni.number}...", getClass)

      dependencies.republicPoliceService
        .getCriminalRecord(prospect.dni).run(dependencies.wsClient)
        .subflatMap(possibleCriminalRecord =>
          dependencies.prospectCriminalRecordValidationService.validateRecord(prospect.dni, possibleCriminalRecord)
        )
  }

  private def validateProspectRating(prospect: Contact): Reader[Dependencies, CustomEitherT[Done]] = Reader {
    dependencies: Dependencies =>
      Logging.info(s"Validating rating for prospect ${prospect.dni.number}...", getClass)

      val score = dependencies.prospectRatingService.rate(prospect.dni)

      dependencies.prospectScoreValidationService
        .validateScore(prospect.dni, score).toCustomEitherT
  }

  private def save(prospect: Contact): Reader[Dependencies, CustomEitherT[Done]] = Reader {
    dependencies: Dependencies =>

      dependencies.contactRepository.save(prospect).map( _ => {
        Logging.info(s"Contact ${prospect.dni.number} saved successfully!!", getClass)
        Done
      })
  }

}

object ProspectProcessingService extends ProspectProcessingService
