package co.com.addi.contactbook.application.services

import akka.Done
import co.com.addi.contactbook.application.commons.Logging
import co.com.addi.contactbook.domain.aliases.CustomEitherT
import co.com.addi.contactbook.domain.enhancements.CustomEitherEnhancement._
import co.com.addi.contactbook.domain.models.{Dni, Prospect}

import scala.util.Random

trait ProspectRatingServiceBase {

  def rate(dni: Dni): Int = Random.nextInt(100) + 1

  def validateRating(prospect: Prospect): CustomEitherT[Done] = {
      Logging.info(s"Validating rating for prospect ${prospect.dni.number}...", getClass)

      prospect.validateScore(rate(prospect.dni)).toCustomEitherT
  }
}

object ProspectRatingService extends ProspectRatingServiceBase
