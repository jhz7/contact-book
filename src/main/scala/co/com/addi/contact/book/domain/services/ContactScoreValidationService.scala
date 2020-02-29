package co.com.addi.contact.book.domain.services

import akka.Done
import co.com.addi.contact.book.application.dtos.{BUSINESS, ErrorDto}
import co.com.addi.contact.book.application.types.CustomEither
import co.com.addi.contact.book.domain.models.Dni

trait ContactScoreValidationService {

  val minimumScore = 60

  def validateScore(dni: Dni, score: Int): CustomEither[Done]

}

object ContactScoreValidationService extends ContactScoreValidationService {

  def validateScore(dni: Dni, score: Int): CustomEither[Done] =
    if(score < minimumScore)
      Left(ErrorDto(BUSINESS, s"The score for the contact ${dni.number} is below the minimum allowed"))
    else Right(Done)

}
