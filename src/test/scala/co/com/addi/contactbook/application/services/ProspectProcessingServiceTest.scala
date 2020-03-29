package co.com.addi.contactbook.application.services

import co.com.addi.contact.book.TestKit
import co.com.addi.contact.book.application.dtos.{APPLICATION, ErrorDto}
import co.com.addi.contact.book.domain.models.{Dni, Person}
import co.com.addi.contact.book.factories.PersonFactory
import co.com.addi.contact.book.tools.FutureTool
import co.com.addi.contactbook.TestKit
import co.com.addi.contactbook.factories.PersonFactory
import co.com.addi.contactbook.tools.FutureTool
import com.softwaremill.quicklens.ModifyPimp
import org.mockito.Mockito.{times, verify}

class ProspectProcessingServiceTest extends TestKit {

  "ProspectProcessingService" should {

    "Process a prospect to determine if is a valid contact" when {

      "The prospect does not exist in temporary directory" must {
        "Return an error" in {

          val dni = PersonFactory.createDni.modify(_.number).setTo("X")

          val dependencies = getFalseDependencies

          doReturn(EitherT.rightT[Task, ErrorDto](None)).when(dependencies.prospectRepository).get(any[Dni]())

          val result = FutureTool.awaitResult(ProspectProcessingService.process(dni).run(dependencies).value.runToFuture)

          result mustBe Left(ErrorDto(APPLICATION, s"The prospect ${dni.number} does not exist in temporary directory"))
          verify(dependencies.prospectRepository, times(1)).get(any[Dni]())
        }
      }

      "The republic identification service does not return any person data" must {
        "Return an error" in {

          val dni = PersonFactory.createDni.modify(_.number).setTo("X")
          val prospect = PersonFactory.createPerson.modify(_.dni).setTo(dni)

          val dependencies = getFalseDependencies

          doReturn(EitherT.rightT[Task, ErrorDto](Some(prospect)))
            .when(dependencies.prospectRepository).get(any[Dni]())
          doReturn(Reader{_: StandaloneAhcWSClient => EitherT.rightT[Task, ErrorDto](None)})
            .when(dependencies.republicIdentificationService).getPerson(any[Dni]())
          doReturn(Reader{_: StandaloneAhcWSClient => EitherT.rightT[Task, ErrorDto](None)})
            .when(dependencies.republicPoliceService).getCriminalRecord(any[Dni]())

          val result = FutureTool.awaitResult(ProspectProcessingService.process(dni).run(dependencies).value.runToFuture)

          result mustBe Left(ErrorDto(APPLICATION, s"The prospect ${dni.number} does not exist in republic identification system"))
          verify(dependencies.prospectRepository, times(1)).get(any[Dni]())
          verify(dependencies.republicIdentificationService, times(1)).getPerson(any[Dni]())
          verify(dependencies.republicPoliceService, times(1)).getCriminalRecord(any[Dni]())
        }
      }

      "The prospect data is not valid against republic identification data" must {
        "Return an error" in {

          val dni = PersonFactory.createDni.modify(_.number).setTo("X")
          val prospect = PersonFactory.createPerson.modify(_.dni).setTo(dni)

          val dependencies = getFalseDependencies

          doReturn(EitherT.rightT[Task, ErrorDto](Some(prospect)))
            .when(dependencies.prospectRepository).get(any[Dni]())
          doReturn(Reader{_: StandaloneAhcWSClient => EitherT.rightT[Task, ErrorDto](Some(prospect))})
            .when(dependencies.republicIdentificationService).getPerson(any[Dni]())
          doReturn(Reader{_: StandaloneAhcWSClient => EitherT.rightT[Task, ErrorDto](None)})
            .when(dependencies.republicPoliceService).getCriminalRecord(any[Dni]())
          doReturn(Left(ErrorDto(APPLICATION, "Fake error validating data")))
            .when(dependencies.prospectDataValidationService).validateData(any[Person](), any[Person]())

          val result = FutureTool.awaitResult(ProspectProcessingService.process(dni).run(dependencies).value.runToFuture)

          result mustBe Left(ErrorDto(APPLICATION, "Fake error validating data"))
          verify(dependencies.prospectRepository, times(1)).get(any[Dni]())
          verify(dependencies.republicIdentificationService, times(1)).getPerson(any[Dni]())
          verify(dependencies.prospectDataValidationService, times(1)).validateData(any[Person](), any[Person]())
          verify(dependencies.republicPoliceService, times(1)).getCriminalRecord(any[Dni]())
        }
      }

      "The criminal record is not valid" must {
        "Return an error" in {

          val dni = PersonFactory.createDni.modify(_.number).setTo("X")
          val prospect = PersonFactory.createPerson.modify(_.dni).setTo(dni)

          val dependencies = getFalseDependencies

          doReturn(EitherT.rightT[Task, ErrorDto](Some(prospect)))
            .when(dependencies.prospectRepository).get(any[Dni]())
          doReturn(Reader{_: StandaloneAhcWSClient => EitherT.rightT[Task, ErrorDto](Some(prospect))})
            .when(dependencies.republicIdentificationService).getPerson(any[Dni]())
          doReturn(Reader{_: StandaloneAhcWSClient => EitherT.rightT[Task, ErrorDto](None)})
            .when(dependencies.republicPoliceService).getCriminalRecord(any[Dni]())
          doReturn(Right(Done))
            .when(dependencies.prospectDataValidationService).validateData(any[Person](), any[Person]())
          doReturn(Left(ErrorDto(APPLICATION, "Fake error validating criminal record")))
            .when(dependencies.prospectCriminalRecordValidationService).validateRecord(any[Dni](), any())

          val result = FutureTool.awaitResult(ProspectProcessingService.process(dni).run(dependencies).value.runToFuture)

          result mustBe Left(ErrorDto(APPLICATION, "Fake error validating criminal record"))
          verify(dependencies.prospectRepository, times(1)).get(any[Dni]())
          verify(dependencies.republicIdentificationService, times(1)).getPerson(any[Dni]())
          verify(dependencies.prospectDataValidationService, times(1)).validateData(any[Person](), any[Person]())
          verify(dependencies.republicPoliceService, times(1)).getCriminalRecord(any[Dni]())
          verify(dependencies.prospectCriminalRecordValidationService, times(1)).validateRecord(any[Dni](), any())
        }
      }

      "The prospect score is below minimum allowed" must {
        "Return an error" in {

          val dni = PersonFactory.createDni.modify(_.number).setTo("X")
          val prospect = PersonFactory.createPerson.modify(_.dni).setTo(dni)
          val score = 20

          val dependencies = getFalseDependencies

          doReturn(EitherT.rightT[Task, ErrorDto](Some(prospect)))
            .when(dependencies.prospectRepository).get(any[Dni]())
          doReturn(Reader{_: StandaloneAhcWSClient => EitherT.rightT[Task, ErrorDto](Some(prospect))})
            .when(dependencies.republicIdentificationService).getPerson(any[Dni]())
          doReturn(Reader{_: StandaloneAhcWSClient => EitherT.rightT[Task, ErrorDto](None)})
            .when(dependencies.republicPoliceService).getCriminalRecord(any[Dni]())
          doReturn(Right(Done))
            .when(dependencies.prospectDataValidationService).validateData(any[Person](), any[Person]())
          doReturn(Right(Done))
            .when(dependencies.prospectCriminalRecordValidationService).validateRecord(any[Dni](), any())
          doReturn(score)
            .when(dependencies.prospectRatingService).rate(any[Dni]())
          doReturn(Left(ErrorDto(APPLICATION, "Fake error validating score")))
            .when(dependencies.prospectScoreValidationService).validateScore(any[Dni](), anyInt)

          val result = FutureTool.awaitResult(ProspectProcessingService.process(dni).run(dependencies).value.runToFuture)

          result mustBe Left(ErrorDto(APPLICATION, "Fake error validating score"))
          verify(dependencies.prospectRepository, times(1)).get(any[Dni]())
          verify(dependencies.republicIdentificationService, times(1)).getPerson(any[Dni]())
          verify(dependencies.prospectDataValidationService, times(1)).validateData(any[Person](), any[Person]())
          verify(dependencies.republicPoliceService, times(1)).getCriminalRecord(any[Dni]())
          verify(dependencies.prospectCriminalRecordValidationService, times(1)).validateRecord(any[Dni](), any())
          verify(dependencies.prospectRatingService, times(1)).rate(any[Dni]())
          verify(dependencies.prospectScoreValidationService, times(1)).validateScore(any[Dni](), anyInt)
        }
      }

      "The contact is saved successfully" must {
        "Return a success response" in {

          val dni = PersonFactory.createDni.modify(_.number).setTo("X")
          val prospect = PersonFactory.createPerson.modify(_.dni).setTo(dni)
          val score = 20

          val dependencies = getFalseDependencies

          doReturn(EitherT.rightT[Task, ErrorDto](Some(prospect)))
            .when(dependencies.prospectRepository).get(any[Dni]())
          doReturn(Reader{_: StandaloneAhcWSClient => EitherT.rightT[Task, ErrorDto](Some(prospect))})
            .when(dependencies.republicIdentificationService).getPerson(any[Dni]())
          doReturn(Reader{_: StandaloneAhcWSClient => EitherT.rightT[Task, ErrorDto](None)})
            .when(dependencies.republicPoliceService).getCriminalRecord(any[Dni]())
          doReturn(Right(Done))
            .when(dependencies.prospectDataValidationService).validateData(any[Person](), any[Person]())
          doReturn(Right(Done))
            .when(dependencies.prospectCriminalRecordValidationService).validateRecord(any[Dni](), any())
          doReturn(score)
            .when(dependencies.prospectRatingService).rate(any[Dni]())
          doReturn(Right(Done))
            .when(dependencies.prospectScoreValidationService).validateScore(any[Dni](), anyInt)
          doReturn(EitherT.rightT[Task, ErrorDto](Done))
            .when(dependencies.contactRepository).save(any[Person]())

          val result = FutureTool.awaitResult(ProspectProcessingService.process(dni).run(dependencies).value.runToFuture)

          result mustBe Right(Done)
          verify(dependencies.prospectRepository, times(1)).get(any[Dni]())
          verify(dependencies.republicIdentificationService, times(1)).getPerson(any[Dni]())
          verify(dependencies.prospectDataValidationService, times(1)).validateData(any[Person](), any[Person]())
          verify(dependencies.republicPoliceService, times(1)).getCriminalRecord(any[Dni]())
          verify(dependencies.prospectCriminalRecordValidationService, times(1)).validateRecord(any[Dni](), any())
          verify(dependencies.prospectRatingService, times(1)).rate(any[Dni]())
          verify(dependencies.prospectScoreValidationService, times(1)).validateScore(any[Dni](), anyInt)
          verify(dependencies.contactRepository, times(1)).save(any[Person]())
        }
      }

    }
  }
}
