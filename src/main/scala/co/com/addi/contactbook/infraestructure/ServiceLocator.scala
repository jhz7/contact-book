package co.com.addi.contactbook.infraestructure

import akka.stream.Materializer
import co.com.addi.contactbook.application.services._
import co.com.addi.contactbook.domain.contracts.repositories._
import co.com.addi.contactbook.domain.contracts.wsclients._
import co.com.addi.contactbook.infraestructure.repositories._
import co.com.addi.contactbook.infraestructure.wsclients._
import play.api.libs.ws.ahc.StandaloneAhcWSClient

class ServiceLocator(implicit var m: Materializer) {

  val wsClient = StandaloneAhcWSClient()

  val prospectRepository: ProspectRepositoryContract = ProspectRepository

  val contactRepository: ContactRepositoryContract = ContactRepository

  val prospectScoringValidationService: ProspectScoringValidationService = ProspectScoringValidationService

  val republicIdentificationService: RepublicIdentificationServiceContract = RepublicIdentificationService(wsClient)

  val republicPoliceService: RepublicPoliceServiceContract = RepublicPoliceService(wsClient)

  val contactPersistenceService: ContactPersistenceService = ContactPersistenceService(contactRepository)

  val prospectPersistenceService: ProspectPersistenceService = ProspectPersistenceService(prospectRepository)

  val prospectDataValidationService: ProspectDataValidationService =
    ProspectDataValidationService(republicIdentificationService, republicPoliceService)

  val prospectProcessingService: ProspectProcessingService =
    ProspectProcessingService(prospectScoringValidationService, prospectDataValidationService, prospectPersistenceService, contactPersistenceService)

}
