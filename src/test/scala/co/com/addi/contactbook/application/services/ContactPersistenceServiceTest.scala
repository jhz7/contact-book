package co.com.addi.contactbook.application.services

import akka.Done
import cats.data.EitherT
import co.com.addi.contactbook.TestKit
import co.com.addi.contactbook.domain.models.Error
import co.com.addi.contactbook.domain.types.APPLICATION
import co.com.addi.contactbook.factories.PersonFactory
import co.com.addi.contactbook.tools.FutureTool.awaitResult
import monix.eval.Task
import org.mockito.Mockito.verify

class ContactPersistenceServiceTest extends TestKit{

  "ContactPersistenceService" should {

    "Save a contact" when {

      val contactToSave = PersonFactory.createContact

      "The saving is successfully" must {
        "Indicate with a success response" in {

          val serviceLocator = getFalseServiceLocator
          val service = ContactPersistenceService(serviceLocator.contactRepository)

          doReturn(EitherT.rightT[Task, Error](Done)).when(service.contactRepository).save(contactToSave)

          val result = awaitResult(service.save(contactToSave).value.runToFuture)

          result mustBe Right(Done)
          verify(service.contactRepository).save(contactToSave)
        }
      }

      "Occurs an error during execution" must {
        "Indicate with an error response" in {

          val serviceLocator = getFalseServiceLocator
          val service = ContactPersistenceService(serviceLocator.contactRepository)

          doReturn(EitherT.leftT[Task, Done](Error(APPLICATION, "Fake error saving contact."))).when(service.contactRepository).save(contactToSave)

          val result = awaitResult(service.save(contactToSave).value.runToFuture)

          result mustBe Left(Error(APPLICATION, "Fake error saving contact."))
          verify(service.contactRepository).save(contactToSave)
        }
      }
    }
  }
}
