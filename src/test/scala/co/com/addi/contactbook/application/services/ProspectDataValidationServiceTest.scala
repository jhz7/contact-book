package co.com.addi.contactbook.application.services

import akka.Done
import cats.data.EitherT
import co.com.addi.contactbook.TestKit
import co.com.addi.contactbook.domain.models.{Dni, Error, Prospect}
import co.com.addi.contactbook.domain.types.{APPLICATION, BUSINESS}
import co.com.addi.contactbook.factories.PersonFactory
import co.com.addi.contactbook.tools.FutureTool.awaitResult
import monix.eval.Task
import org.mockito.Mockito.{never, verify}

class ProspectDataValidationServiceTest extends TestKit{

  "ProspectDataValidationService" should {

    "Validate" when {

      "The prospect is valid" must {
        "Indicate it with a success response" in {

          val serviceLocator = getFalseServiceLocator

          val prospect = spy(Prospect(firstName = "", lastName = "", PersonFactory.createDni))
          val existsCriminalRecord = false
          val prospectFromRepublicIdentificationService = Some(PersonFactory.createProspect)
          val service = ProspectDataValidationService(serviceLocator.republicIdentificationService, serviceLocator.republicPoliceService)

          doReturn(Right(Done)).when(prospect).validateDataEquality(any[Prospect]())
          doReturn(EitherT.rightT[Task, Error](prospectFromRepublicIdentificationService))
            .when(serviceLocator.republicIdentificationService).getProspectData(any[Dni]())
          doReturn(EitherT.rightT[Task, Error](existsCriminalRecord))
            .when(serviceLocator.republicPoliceService).existsCriminalRecord(any[Dni]())

          val result = awaitResult(service.validate(prospect).value.runToFuture)

          result mustBe Right(Done)
          verify(prospect).validateDataEquality(any[Prospect]())
          verify(serviceLocator.republicIdentificationService).getProspectData(any[Dni]())
          verify(serviceLocator.republicPoliceService).existsCriminalRecord(any[Dni]())
        }
      }

      "The prospect data does not match with the provided republic identification service" must {
        "Indicate it with an error response" in {

          val serviceLocator = getFalseServiceLocator

          val prospect = spy(Prospect(firstName = "", lastName = "", PersonFactory.createDni))
          val existsCriminalRecord = false
          val prospectFromRepublicIdentificationService = Some(PersonFactory.createProspect)
          val service = ProspectDataValidationService(serviceLocator.republicIdentificationService, serviceLocator.republicPoliceService)

          doReturn(Left(Error(APPLICATION, "Fake error in validateDataEquality"))).when(prospect).validateDataEquality(any[Prospect]())
          doReturn(EitherT.rightT[Task, Error](prospectFromRepublicIdentificationService))
            .when(serviceLocator.republicIdentificationService).getProspectData(any[Dni]())
          doReturn(EitherT.rightT[Task, Error](existsCriminalRecord))
            .when(serviceLocator.republicPoliceService).existsCriminalRecord(any[Dni]())

          val result = awaitResult(service.validate(prospect).value.runToFuture)

          result mustBe Left(Error(APPLICATION, "Fake error in validateDataEquality"))
          verify(prospect).validateDataEquality(any[Prospect]())
          verify(serviceLocator.republicIdentificationService).getProspectData(any[Dni]())
          verify(serviceLocator.republicPoliceService).existsCriminalRecord(any[Dni]())
        }
      }

      "The prospect data does not exist republic identification service" must {
        "Indicate it with an error response" in {

          val serviceLocator = getFalseServiceLocator

          val prospect = spy(Prospect(firstName = "", lastName = "", PersonFactory.createDni))
          val existsCriminalRecord = false
          val prospectFromRepublicIdentificationService = None
          val service = ProspectDataValidationService(serviceLocator.republicIdentificationService, serviceLocator.republicPoliceService)

          doReturn(EitherT.rightT[Task, Error](prospectFromRepublicIdentificationService))
            .when(serviceLocator.republicIdentificationService).getProspectData(any[Dni]())
          doReturn(EitherT.rightT[Task, Error](existsCriminalRecord))
            .when(serviceLocator.republicPoliceService).existsCriminalRecord(any[Dni]())

          val result = awaitResult(service.validate(prospect).value.runToFuture)

          result mustBe Left(Error(APPLICATION, s"The prospect ${prospect.dni.number} does not exist in republic identification system"))
          verify(prospect, never()).validateDataEquality(any[Prospect]())
          verify(serviceLocator.republicIdentificationService).getProspectData(any[Dni]())
          verify(serviceLocator.republicPoliceService).existsCriminalRecord(any[Dni]())
        }
      }

      "The prospect has criminal record" must {
        "Indicate it with an error response" in {

          val serviceLocator = getFalseServiceLocator

          val prospect = spy(Prospect(firstName = "", lastName = "", PersonFactory.createDni))
          val existsCriminalRecord = true
          val prospectFromRepublicIdentificationService = Some(PersonFactory.createProspect)
          val service = ProspectDataValidationService(serviceLocator.republicIdentificationService, serviceLocator.republicPoliceService)

          doReturn(Right(Done)).when(prospect).validateDataEquality(any[Prospect]())
          doReturn(EitherT.rightT[Task, Error](prospectFromRepublicIdentificationService))
            .when(serviceLocator.republicIdentificationService).getProspectData(any[Dni]())
          doReturn(EitherT.rightT[Task, Error](existsCriminalRecord))
            .when(serviceLocator.republicPoliceService).existsCriminalRecord(any[Dni]())

          val result = awaitResult(service.validate(prospect).value.runToFuture)

          result mustBe Left(Error(BUSINESS, s"Exists criminal record for the prospect ${prospect.dni.number}"))
          verify(prospect).validateDataEquality(any[Prospect]())
          verify(serviceLocator.republicIdentificationService).getProspectData(any[Dni]())
          verify(serviceLocator.republicPoliceService).existsCriminalRecord(any[Dni]())
        }
      }
    }
  }
}
