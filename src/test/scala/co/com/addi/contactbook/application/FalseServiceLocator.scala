package co.com.addi.contactbook.application

import akka.stream.Materializer
import co.com.addi.contactbook.application.services._
import co.com.addi.contactbook.domain.contracts.repositories._
import co.com.addi.contactbook.domain.contracts.wsclients._
import co.com.addi.contactbook.infraestructure.ServiceLocator
import org.specs2.mock.Mockito

class FalseServiceLocator(implicit m: Materializer) extends ServiceLocator() with Mockito{

  override val contactRepository: ContactRepositoryContract = mock[ContactRepositoryContract]
  override val prospectRepository: ProspectRepositoryContract = mock[ProspectRepositoryContract]
  override val prospectProcessingService: ProspectProcessingService = mock[ProspectProcessingService]
  override val contactPersistenceService: ContactPersistenceService = mock[ContactPersistenceService]
  override val prospectPersistenceService: ProspectPersistenceService = mock[ProspectPersistenceService]
  override val republicPoliceService: RepublicPoliceServiceContract = mock[RepublicPoliceServiceContract]
  override val prospectScoringService: ProspectScoringValidationService = mock[ProspectScoringValidationService]
  override val prospectDataValidationService: ProspectDataValidationService = mock[ProspectDataValidationService]
  override val republicIdentificationService: RepublicIdentificationServiceContract = mock[RepublicIdentificationServiceContract]
}
