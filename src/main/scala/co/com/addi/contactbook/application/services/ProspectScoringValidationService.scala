package co.com.addi.contactbook.application.services

import akka.Done
import co.com.addi.contactbook.application.commons.Logging
import co.com.addi.contactbook.domain.aliases.CustomEitherT
import co.com.addi.contactbook.domain.enhancements.CustomEitherEnhancement._
import co.com.addi.contactbook.domain.models.Prospect

import scala.util.Random

trait ProspectScoringValidationService {

  def validate(prospect: Prospect): CustomEitherT[Done] = {
    Logging.info(s"Validating rating for prospect ${prospect.dni.number}...", getClass)

    val score: Int = Random.nextInt(100)
    prospect.validateScore(score).toCustomEitherT
  }

}

object ProspectScoringValidationService extends ProspectScoringValidationService
