package co.com.addi.contactbook.application

import akka.actor.ActorSystem
import co.com.addi.contactbook.application.services.{ProspectProcessingService, ProspectRatingServiceBase, ValidationProspectVsRepublicSystemService}
import co.com.addi.contactbook.domain.contracts.repositories.{ContactRepositoryContract, ProspectRepositoryContract}
import co.com.addi.contactbook.domain.contracts.wsclients.{RepublicIdentificationServiceContract, RepublicPoliceServiceContract}
import co.com.addi.contactbook.infraestructure.ServiceLocator
import org.specs2.mock.Mockito

class FalseDependencies(implicit system: ActorSystem) extends ServiceLocator() with Mockito{
  override val republicIdentificationService = mock[RepublicIdentificationServiceContract]
  override val republicPoliceService = mock[RepublicPoliceServiceContract]
  override val prospectRepository = mock[ProspectRepositoryContract]
  override val contactRepository = mock[ContactRepositoryContract]
  override val prospectRatingService = mock[ProspectRatingServiceBase]
  override val validationProspectVsRepublicSystemService = mock[ValidationProspectVsRepublicSystemService]
  override val prospectProcessingService = mock[ProspectProcessingService]
}
