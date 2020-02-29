package co.com.addi.contact.book.infraestructure.repositories

import akka.Done
import co.com.addi.contact.book.TestKit
import co.com.addi.contact.book.factories.PersonFactory
import co.com.addi.contact.book.tools.FutureTool

class ContactRepositoryTest extends TestKit {

  "ContactRepository" should {

    "Save a person to contact directory" when {

      "The execution goes well" must {
        "Save the person" in {
          val person = PersonFactory.createPerson

          val result = FutureTool.awaitResult(ContactRepository.save(person).value.runToFuture)

          result mustBe Right(Done)
        }
      }
    }
  }
}
