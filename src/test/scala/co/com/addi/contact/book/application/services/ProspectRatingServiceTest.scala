package co.com.addi.contact.book.application.services

import co.com.addi.contact.book.TestKit
import co.com.addi.contact.book.factories.PersonFactory

class ProspectRatingServiceTest extends TestKit {

  "ProspectRatingService" should {

    "Rating randomly a prospect" must {
      "Return the generated score" in {
        val dni = PersonFactory.createDni

        val result = ProspectRatingService.rate(dni)

        assert(result <= 100)
        assert(result > 0)
      }
    }
  }
}
