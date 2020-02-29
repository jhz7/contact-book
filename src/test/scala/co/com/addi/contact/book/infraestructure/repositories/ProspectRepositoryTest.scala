package co.com.addi.contact.book.infraestructure.repositories

import co.com.addi.contact.book.TestKit
import co.com.addi.contact.book.domain.models.Person
import co.com.addi.contact.book.factories.PersonFactory
import co.com.addi.contact.book.tools.FutureTool
import com.softwaremill.quicklens.ModifyPimp

class ProspectRepositoryTest extends TestKit {

  "ProspectRepository" should {

    "Get a prospect person from temporary directory" when {

      "The prospect exists" must {
        "Get the prospect" in {

          val dni = PersonFactory.createDni.modify(_.number).setTo("1")

          val result = FutureTool.awaitResult(ProspectRepository.get(dni).value.runToFuture)

          result match {
            case Right(prospect) => prospect.map(_.isInstanceOf[Person])
            case Left(_) => fail("Should return a prospect")
          }
        }
      }

      "The prospect does not exist" must {
        "Not return data" in {

          val dni = PersonFactory.createDni.modify(_.number).setTo("50")

          val result = FutureTool.awaitResult(ProspectRepository.get(dni).value.runToFuture)

          result mustBe Right(None)
        }
      }
    }
  }
}
