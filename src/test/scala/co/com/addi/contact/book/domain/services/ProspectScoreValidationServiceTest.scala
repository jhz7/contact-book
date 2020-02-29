package co.com.addi.contact.book.domain.services

import akka.Done
import co.com.addi.contact.book.TestKit
import co.com.addi.contact.book.application.dtos.{BUSINESS, ErrorDto}
import co.com.addi.contact.book.factories.PersonFactory

class ProspectScoreValidationServiceTest extends TestKit {

  "ProspectScoreValidationService" should {

    "Validate score for prospect" when {

      "The score is below minimum allowed" must {
        "Return an error" in {
          val score = 10
          val dni = PersonFactory.createDni

          val result = ProspectScoreValidationService.validateScore(dni, score)

          result mustBe Left(ErrorDto(BUSINESS, s"The score for the prospect ${dni.number} is below the minimum allowed"))
        }
      }

      "The score is above or equal to minimum allowed" must {
        "Return a success response" in {
          val score = 60
          val dni = PersonFactory.createDni

          val result = ProspectScoreValidationService.validateScore(dni, score)

          result mustBe Right(Done)
        }
      }
    }
  }
}
