package co.com.addi.contactbook.application.services

import akka.Done
import co.com.addi.contactbook.TestKit
import co.com.addi.contactbook.domain.models.{Error, Prospect}
import co.com.addi.contactbook.domain.types.APPLICATION
import co.com.addi.contactbook.factories.PersonFactory
import co.com.addi.contactbook.tools.FutureTool.awaitResult

class ProspectScoringValidationServiceTest extends TestKit{

  "ProspectScoringValidationService" should {

    "Start prospect score validation" when {

      "The score is valid" must {
        "Indicate it with a success response" in {
          val expectedResult = Right(Done)
          val prospect = spy(Prospect(firstName = "", lastName = "", dni = PersonFactory.createDni))

          doReturn(expectedResult).when(prospect).validateScore(anyInt)

          val result = awaitResult(ProspectScoringValidationService.validate(prospect).value.runToFuture)

          result mustBe expectedResult
        }
      }

      "The score is NOT valid" must {
        "Indicate it with an error response" in {
          val expectedResult = Left(Error(APPLICATION, "Fake error..."))
          val prospect = spy(Prospect(firstName = "", lastName = "", dni = PersonFactory.createDni))

          doReturn(expectedResult).when(prospect).validateScore(anyInt)

          val result = awaitResult(ProspectScoringValidationService.validate(prospect).value.runToFuture)

          result mustBe expectedResult
        }
      }
    }
  }
}
