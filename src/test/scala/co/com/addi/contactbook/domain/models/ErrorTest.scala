package co.com.addi.contactbook.domain.models

import co.com.addi.contactbook.TestKit
import co.com.addi.contactbook.domain.types.{APPLICATION, BUSINESS, TECHNICAL}

class ErrorTest extends TestKit{

  "Error" should {

    "Unify a list of errors" when {

      val message1 = "Message 1"
      val message2 = "Message 2"
      val unifiedMessage = s"$message1. $message2"

      "The list contains at least a BUSINESS error" must {
        "Return a BUSINESS error with unified message" in {

          val error1 = Error(BUSINESS, message1)
          val error2 = Error(APPLICATION, message2)
          val errors = List(error1, error2)
          val expectedUnifiedError = Error(BUSINESS, unifiedMessage)

          val result = Error.unifyErrors(errors)

          expectedUnifiedError mustBe result
        }
      }

      "The list contains at least an APPLICATION and any BUSINESS error" must {
        "Return an APPLICATION error with unified message" in {

          val error1 = Error(TECHNICAL, message1)
          val error2 = Error(APPLICATION, message2)
          val errors = List(error1, error2)
          val expectedUnifiedError = Error(APPLICATION, unifiedMessage)

          val result = Error.unifyErrors(errors)

          expectedUnifiedError mustBe result
        }
      }

      "The list contains at least a TECHNICAL and any BUSINESS or APPLICATION error" must {
        "Return a TECHNICAL error with unified message" in {

          val error1 = Error(TECHNICAL, message1)
          val error2 = Error(TECHNICAL, message2)
          val errors = List(error1, error2)
          val expectedUnifiedError = Error(TECHNICAL, unifiedMessage)

          val result = Error.unifyErrors(errors)

          expectedUnifiedError mustBe result
        }
      }
    }
  }
}
