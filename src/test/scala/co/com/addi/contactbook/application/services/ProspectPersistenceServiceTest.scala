package co.com.addi.contactbook.application.services

import cats.data.EitherT
import co.com.addi.contactbook.TestKit
import co.com.addi.contactbook.domain.models.{Error, Prospect}
import co.com.addi.contactbook.domain.types.APPLICATION
import co.com.addi.contactbook.factories.PersonFactory
import co.com.addi.contactbook.tools.FutureTool.awaitResult
import monix.eval.Task
import org.mockito.Mockito.verify

class ProspectPersistenceServiceTest extends TestKit{

  "ProspectPersistenceService" should {

    "Get the prospect data" when {

      "The prospect exists in temporary directory" must {
        "Return the gotten prospect" in {

          val serviceLocator = getFalseServiceLocator

          val dni = PersonFactory.createDni
          val prospect = PersonFactory.createProspect
          val prospectPersistenceService = ProspectPersistenceService(serviceLocator.prospectRepository)

          doReturn(EitherT.rightT[Task, Error](Some(prospect)))
            .when(serviceLocator.prospectRepository).get(dni)

          val result = awaitResult(
            prospectPersistenceService.get(dni).value.runToFuture)

          result mustBe Right(prospect)
          verify(serviceLocator.prospectRepository).get(dni)
        }
      }

      "The prospect does NOT exist in temporary directory" must {
        "Generate an error response" in {

          val serviceLocator = getFalseServiceLocator

          val dni = PersonFactory.createDni
          val prospectPersistenceService = ProspectPersistenceService(serviceLocator.prospectRepository)

          doReturn(EitherT.rightT[Task, Error](None))
            .when(serviceLocator.prospectRepository).get(dni)

          val result = awaitResult(
            prospectPersistenceService.get(dni).value.runToFuture)

          result mustBe Left(Error(APPLICATION, s"The prospect ${dni.number} does not exist in temporary directory"))
          verify(serviceLocator.prospectRepository).get(dni)
        }
      }

      "Occurs an error in prospect repository" must {
        "Generate an error response" in {

          val serviceLocator = getFalseServiceLocator

          val dni = PersonFactory.createDni
          val expectedError = Error(APPLICATION, "Fake error")
          val prospectPersistenceService = ProspectPersistenceService(serviceLocator.prospectRepository)

          doReturn(EitherT.leftT[Task, Option[Prospect]](expectedError))
            .when(serviceLocator.prospectRepository).get(dni)

          val result = awaitResult(
            prospectPersistenceService.get(dni).value.runToFuture)

          result mustBe Left(expectedError)
          verify(serviceLocator.prospectRepository).get(dni)
        }
      }
    }
  }
}
