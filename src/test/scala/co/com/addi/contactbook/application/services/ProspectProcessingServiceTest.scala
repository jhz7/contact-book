package co.com.addi.contactbook.application.services

import akka.Done
import cats.data.EitherT
import co.com.addi.contactbook.TestKit
import co.com.addi.contactbook.domain.models.{Contact, Dni, Error, Prospect}
import co.com.addi.contactbook.domain.types.APPLICATION
import co.com.addi.contactbook.factories.PersonFactory
import co.com.addi.contactbook.tools.FutureTool.awaitResult
import monix.eval.Task
import org.mockito.Mockito.{never, verify}

class ProspectProcessingServiceTest extends TestKit{

  "ProspectProcessingService" should {

    "The contact is saved successfully" must {
      "Indicate it with a success response" in {

        val serviceLocator = getFalseServiceLocator

        val dni = PersonFactory.createDni
        val prospectProcessingService = ProspectProcessingService(
          serviceLocator.prospectScoringValidationService,
          serviceLocator.prospectDataValidationService,
          serviceLocator.prospectPersistenceService,
          serviceLocator.contactPersistenceService
        )

        doReturn(EitherT.rightT[Task, Error](PersonFactory.createProspect)).when(serviceLocator.prospectPersistenceService).get(any[Dni]())
        doReturn(EitherT.rightT[Task, Error](Done)).when(serviceLocator.prospectDataValidationService).validate(any[Prospect]())
        doReturn(EitherT.rightT[Task, Error](Done)).when(serviceLocator.prospectScoringValidationService).validate(any[Prospect]())
        doReturn(EitherT.rightT[Task, Error](Done)).when(serviceLocator.contactPersistenceService).save(any[Contact]())

        val result = awaitResult(
          prospectProcessingService.becomeContact(dni).value.runToFuture)

        result mustBe Right(Done)
        verify(serviceLocator.prospectPersistenceService).get(any[Dni]())
        verify(serviceLocator.prospectDataValidationService).validate(any[Prospect]())
        verify(serviceLocator.prospectScoringValidationService).validate(any[Prospect]())
        verify(serviceLocator.contactPersistenceService).save(any[Contact]())
      }
    }

    "Occurs an error getting prospect" must {
      "Indicate it with an error response" in {

        val serviceLocator = getFalseServiceLocator

        val dni = PersonFactory.createDni
        val prospectProcessingService = ProspectProcessingService(
          serviceLocator.prospectScoringValidationService,
          serviceLocator.prospectDataValidationService,
          serviceLocator.prospectPersistenceService,
          serviceLocator.contactPersistenceService
        )

        doReturn(EitherT.leftT[Task, Done](Error(APPLICATION, "Fake error")))
          .when(serviceLocator.prospectPersistenceService).get(any[Dni]())

        val result = awaitResult(
          prospectProcessingService.becomeContact(dni).value.runToFuture)

        result mustBe Left(Error(APPLICATION, "Fake error"))
        verify(serviceLocator.prospectPersistenceService).get(any[Dni]())
        verify(serviceLocator.prospectDataValidationService, never()).validate(any[Prospect]())
        verify(serviceLocator.prospectScoringValidationService, never()).validate(any[Prospect]())
        verify(serviceLocator.contactPersistenceService, never()).save(any[Contact]())
      }
    }
  }
}
