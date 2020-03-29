package co.com.addi.contact.book.domain.services

import akka.Done
import co.com.addi.contact.book.application.dtos.BUSINESS
import co.com.addi.contact.book.application.types.CustomEither
import co.com.addi.contact.book.domain.models.{BUSINESS, Dni, Error}

trait ProspectScoreValidationService {

  val minimumScore = 60

  def validateScore(dni: Dni, score: Int): CustomEither[Done]

}

object ProspectScoreValidationService extends ProspectScoreValidationService {

  def validateScore(dni: Dni, score: Int): CustomEither[Done] =
    if(score < minimumScore)
      Left(Error(BUSINESS, s"The score for the prospect ${dni.number} is below the minimum allowed"))
    else Right(Done)

}
