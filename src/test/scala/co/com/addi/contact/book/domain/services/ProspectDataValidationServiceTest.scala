package co.com.addi.contact.book.domain.services

import akka.Done
import co.com.addi.contact.book.TestKit
import co.com.addi.contact.book.application.dtos.ErrorDto
import co.com.addi.contact.book.factories.PersonFactory
import com.softwaremill.quicklens.ModifyPimp

class ProspectDataValidationServiceTest extends TestKit {

  "ProspectDataValidationService" should {

    "Validate personal data for prospect" when {

      "The prospect is valid against identification service data" must {
        "Return a success response" in {
          val prospec = PersonFactory.createPerson
          val identificationServiceData = PersonFactory.createPerson

          val result = ProspectDataValidationService.validateData(prospec, identificationServiceData)

          result mustBe Right(Done)
        }
      }

      "The prospect is not valid against identification service data" must {
        "Return an error" in {
          val prospec = PersonFactory.createPerson
            .modify(_.firstName).setTo("Different name")
          val identificationServiceData = PersonFactory.createPerson

          val result = ProspectDataValidationService.validateData(prospec, identificationServiceData)

          result mustBe Left(_: ErrorDto)
        }
      }
    }
  }
}
