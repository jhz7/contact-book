package co.com.addi.contactbook.domain.models

import akka.Done
import co.com.addi.contactbook.TestKit
import co.com.addi.contactbook.domain.types.BUSINESS
import co.com.addi.contactbook.factories.PersonFactory

class ProspectTest extends TestKit{

  "Prospect" should {

    "Validate score" when {

      val firstName = "Jhon"
      val lastName = "Zambrano"
      val dni = PersonFactory.createDni
      val prospect = Prospect(firstName, lastName, dni)

      "The score is grater or equal the minimum allowed" must {
        "Indicate it with a success response" in {
          val score = 60
          val result = prospect.validateScore(score)

          result mustBe Right(Done)
        }
      }

      "The score is below the minimum allowed" must {
        "Indicate it with an error response" in {
          val score = 59
          val result = prospect.validateScore(score)

          result mustBe Left(Error(BUSINESS, s"The score for the prospect ${dni.number} is below the minimum allowed"))
        }
      }
    }

    "Validate data equality" when {

      "The data is valid" must {
        "Indicate it with a success response" in {

          val firstName = "Jhon"
          val lastName = "Zambrano"
          val dni = PersonFactory.createDni
          val prospect = Prospect(firstName, lastName, dni)
          val prospectToValidate = Prospect(firstName, lastName, dni)

          val result = prospect.validateDataEquality(prospectToValidate)

          result mustBe Right(Done)
        }
      }

      "The data is valid" must {
        "Indicate it with a success response" in {

          val firstName1 = "Jhon"
          val firstName2 = "Edwin"
          val lastName = "Zambrano"
          val dni = PersonFactory.createDni
          val prospect = Prospect(firstName1, lastName, dni)
          val prospectToValidate = Prospect(firstName2, lastName, dni)

          val result = prospect.validateDataEquality(prospectToValidate)

          result mustBe Left(Error(BUSINESS, s"The 'First Name' value is not valid for prospect ${dni.number}"))
        }
      }
    }
  }
}
