package co.com.addi.contactbook.infraestructure.repositories

import akka.Done
import co.com.addi.contactbook.TestKit
import co.com.addi.contactbook.factories.PersonFactory
import co.com.addi.contactbook.tools.FutureTool.awaitResult

class ContactRepositoryTest extends TestKit {

  "ContactRepository" should {

    "Save a person to contact directory" when {

      "The execution goes well" must {
        "Save the person" in {
          val person = PersonFactory.createContact

          val result = awaitResult(ContactRepository.save(person).value.runToFuture)

          result mustBe Right(Done)
        }
      }
    }
  }
}
