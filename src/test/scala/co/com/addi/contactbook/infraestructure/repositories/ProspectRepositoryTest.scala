package co.com.addi.contactbook.infraestructure.repositories

import co.com.addi.contactbook.TestKit
import co.com.addi.contactbook.domain.models.Prospect
import co.com.addi.contactbook.factories.PersonFactory
import co.com.addi.contactbook.tools.FutureTool.awaitResult
import com.softwaremill.quicklens.ModifyPimp

class ProspectRepositoryTest extends TestKit {

  "ProspectRepository" should {

    "Get a prospect person from temporary directory" when {

      "The prospect exists" must {
        "Get the prospect" in {

          val dni = PersonFactory.createDni.modify(_.number).setTo("1")

          val result = awaitResult(ProspectRepository.get(dni).value.runToFuture)

          result match {
            case Right(prospect) => prospect.map(_.isInstanceOf[Prospect])
            case Left(_) => fail("Should return a prospect")
          }
        }
      }

      "The prospect does not exist" must {
        "Not return data" in {

          val dni = PersonFactory.createDni.modify(_.number).setTo("50")

          val result = awaitResult(ProspectRepository.get(dni).value.runToFuture)

          result mustBe Right(None)
        }
      }
    }
  }
}
