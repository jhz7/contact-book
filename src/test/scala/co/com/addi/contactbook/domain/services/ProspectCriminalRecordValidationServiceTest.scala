package co.com.addi.contactbook.domain.services

import co.com.addi.contact.book.TestKit
import co.com.addi.contact.book.application.dtos.{BUSINESS, ErrorDto}
import co.com.addi.contact.book.factories.{CriminalRecordFactory, PersonFactory}
import co.com.addi.contactbook.TestKit
import co.com.addi.contactbook.factories.{CriminalRecordFactory, PersonFactory}
import com.softwaremill.quicklens.ModifyPimp

class ProspectCriminalRecordValidationServiceTest extends TestKit {

  "ProspectCriminalRecordValidationService" should {

    "Validate criminal record" when {

      "The criminal record does not exist" must {
        "Return success response" in {
          val criminalRecord = None
          val dni = PersonFactory.createDni

          val result = ProspectCriminalRecordValidationService.validateRecord(dni, criminalRecord)

          result mustBe Right(Done)
        }
      }

      "The criminal record exists but does not have registries" must {
        "Return success response" in {
          val criminalRecord = Some(
            CriminalRecordFactory.getInstance.modify(_.descriptions).setTo(Nil)
          )
          val dni = PersonFactory.createDni

          val result = ProspectCriminalRecordValidationService.validateRecord(dni, criminalRecord)

          result mustBe Right(Done)
        }
      }

      "The criminal record exists and has registries" must {
        "Return error" in {
          val criminalRecord = Some(
            CriminalRecordFactory.getInstance.modify(_.descriptions).setTo(List("Fake desc"))
          )
          val dni = PersonFactory.createDni

          val result = ProspectCriminalRecordValidationService.validateRecord(dni, criminalRecord)

          result mustBe Left(ErrorDto(BUSINESS, s"The criminal record for the prospect ${dni.number} is not valid"))
        }
      }
    }
  }
}
