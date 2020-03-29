package co.com.addi.contactbook.domain.services

import co.com.addi.contact.book.TestKit
import co.com.addi.contact.book.application.dtos.{BUSINESS, ErrorDto}
import co.com.addi.contact.book.factories.PersonFactory
import co.com.addi.contactbook.TestKit
import co.com.addi.contactbook.factories.PersonFactory

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
