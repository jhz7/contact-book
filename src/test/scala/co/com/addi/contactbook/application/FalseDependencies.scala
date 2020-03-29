package co.com.addi.contactbook.application

import akka.actor.ActorSystem
import co.com.addi.contact.book.application.services.{ProspectProcessingService, ProspectRatingService}
import co.com.addi.contact.book.domain.contracts.{ContactBaseRepository, ProspectBaseRepository}
import co.com.addi.contact.book.domain.services.{ProspectCriminalRecordValidationService, ProspectDataValidationService, ProspectScoreValidationService}
import co.com.addi.contact.book.infraestructure.wsclients.{RepublicIdentificationService, RepublicPoliceService}
import org.specs2.mock.Mockito

class FalseDependencies(implicit system: ActorSystem) extends Dependencies() with Mockito{
  override val republicIdentificationService: RepublicIdentificationService = mock[RepublicIdentificationService]
  override val republicPoliceService: RepublicPoliceService = mock[RepublicPoliceService]
  override val prospectRepository: ProspectBaseRepository = mock[ProspectBaseRepository]
  override val contactRepository: ContactBaseRepository = mock[ContactBaseRepository]
  override val prospectRatingService: ProspectRatingService = mock[ProspectRatingService]
  override val prospectDataValidationService: ProspectDataValidationService = mock[ProspectDataValidationService]
  override val prospectCriminalRecordValidationService: ProspectCriminalRecordValidationService = mock[ProspectCriminalRecordValidationService]
  override val prospectScoreValidationService: ProspectScoreValidationService = mock[ProspectScoreValidationService]
  override val prospectProcessingService: ProspectProcessingService = mock[ProspectProcessingService]
}
