package co.com.addi.contact.book.application.services

import akka.Done
import cats.data.{EitherT, Reader}
import co.com.addi.contact.book.application.Dependencies
import co.com.addi.contact.book.application.dtos.{APPLICATION, ErrorDto}
import co.com.addi.contact.book.application.enhancements.CustomEitherEnhancement._
import co.com.addi.contact.book.application.types.{CustomEither, CustomEitherT}
import co.com.addi.contact.book.domain.models.{Dni, Person}
import co.com.addi.contact.book.infraestructure.logger.Logging
import monix.eval.Task

trait ProspectProcessingService {

  def process(dni: Dni): Reader[Dependencies, CustomEitherT[Done]]
}

object ProspectProcessingService extends ProspectProcessingService {

  def process(dni: Dni): Reader[Dependencies, CustomEitherT[Done]] = Reader {
    dependencies: Dependencies =>
      Logging.info("El logger funciona", getClass)

      val processing: CustomEitherT[Done] =
        for {
          prospect <- getProspect(dni).run(dependencies)
//          _ <- validateProspectInfo(prospect).run(dependencies)
          _ <- validateProspectData(prospect).run(dependencies)
          _ <- validateProspectCriminalRecord(prospect).run(dependencies)
          _ <- validateProspectRating(prospect).run(dependencies)
          _ <- save(prospect).run(dependencies)
        } yield Done

      processing.leftMap(error => {
        Logging.error(error.message, None, getClass)
        error
      })
  }

  private def getProspect(dni: Dni): Reader[Dependencies, CustomEitherT[Person]] = Reader {
    dependencies: Dependencies =>
      dependencies.prospectRepository.get(dni) flatMap{
        case Some(person) => EitherT.rightT[Task, ErrorDto](person)
        case None         => EitherT.leftT(ErrorDto(APPLICATION, s"The prospect ${dni.number} does not exist in temporary directory"))
      }
  }

//  private def validateProspectInfo(prospect: Person): Reader[Dependencies, CustomEitherT[Done]] = Reader {
//    dependencies: Dependencies =>
//
//      val validationData = validateProspectData(prospect).run(dependencies).value
//      val validationCriminalRecord = validateProspectCriminalRecord(prospect).run(dependencies).value
//
//      val validationExecution: Task[CustomEither[Done]] =
//        for {
//          _ <- validationData
//          _ <- validationCriminalRecord
//        } yield Right(Done)
//
//      EitherT(validationExecution)
//  }

  private def validateProspectData(prospect: Person): Reader[Dependencies, CustomEitherT[Done]] = Reader {
    dependencies: Dependencies =>

      dependencies.republicIdentificationService
        .getPerson(prospect.dni).run(dependencies.wsClient)
        .subflatMap{
          case Some(person) => dependencies.prospectDataValidationService.validateData(prospect, person)
          case None         => Left(ErrorDto(APPLICATION, s"The prospect ${prospect.dni.number} does not exist in republic identification system"))
        }
  }

  private def validateProspectCriminalRecord(prospect: Person): Reader[Dependencies, CustomEitherT[Done]] = Reader {
    dependencies: Dependencies =>

      dependencies.republicPoliceService
        .getCriminalRecord(prospect.dni).run(dependencies.wsClient)
        .subflatMap(possibleCriminalRecord =>
          dependencies.prospectCriminalRecordValidationService.validateRecord(prospect.dni, possibleCriminalRecord)
        )
  }

  private def validateProspectRating(prospect: Person): Reader[Dependencies, CustomEitherT[Done]] = Reader {
    dependencies: Dependencies =>

      val score = dependencies.prospectRatingService.rate(prospect.dni)

      dependencies.prospectScoreValidationService
        .validateScore(prospect.dni, score).toCustomEitherT
  }

  private def save(prospect: Person): Reader[Dependencies, CustomEitherT[Done]] = Reader {
    dependencies: Dependencies =>

      dependencies.contactRepository
        .save(prospect).map(
        _ => {
          Logging.info(s"Contact ${prospect.dni.number} saved successfully!!", getClass)
          Done
        })
  }

}
