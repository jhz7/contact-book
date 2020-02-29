package co.com.addi.contact.book.application.services

import akka.Done
import cats.data.{EitherT, Reader}
import co.com.addi.contact.book.application.Dependencies
import co.com.addi.contact.book.application.dtos.{APPLICATION, ErrorDto}
import co.com.addi.contact.book.application.types.CustomEitherT
import co.com.addi.contact.book.domain.models.{Dni, Person}
import monix.eval.Task
import co.com.addi.contact.book.application.enhancements.CustomEitherEnhancement._

trait ProspectProcessingService {

}

object ProspectProcessingService extends ProspectProcessingService {

  def process(dni: Dni): Reader[Dependencies, CustomEitherT[Done]] = Reader {
    dependencies: Dependencies =>


  }

  private def getProspect(dni: Dni): Reader[Dependencies, CustomEitherT[Person]] = Reader {
    dependencies: Dependencies =>
      dependencies.prospectRepository.get(dni) flatMap{
        case Some(person) => EitherT.rightT[Task, ErrorDto](person)
        case None         => EitherT.leftT(ErrorDto(APPLICATION, s"The prospect ${dni.number} does not exist in temporary directory"))
      }
  }

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

}
