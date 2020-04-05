package co.com.addi.contactbook.application.services

import co.com.addi.contactbook.TestKit
import co.com.addi.contactbook.factories.PersonFactory

class ProspectScoringValidationServiceTest extends TestKit {

  "ProspectRatingService" should {

    "Rating randomly a prospect" must {
      "Return the generated score" in {
        val dni = PersonFactory.createDni

        val result = ProspectScoringValidationService.rate(dni)

        assert(result <= 100)
        assert(result > 0)
      }
    }
  }
}
