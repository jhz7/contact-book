package co.com.addi.contactbook.infraestructure

import akka.actor.ActorSystem
import akka.stream.{Materializer, SystemMaterializer}
import co.com.addi.contactbook.application.services._
import co.com.addi.contactbook.domain.contracts.repositories.{ContactRepositoryContract, ProspectRepositoryContract}
import co.com.addi.contactbook.domain.contracts.wsclients.{RepublicIdentificationServiceContract, RepublicPoliceServiceContract}
import co.com.addi.contactbook.infraestructure.repositories.{ContactRepository, ProspectRepository}
import co.com.addi.contactbook.infraestructure.wsclients.{RepublicIdentificationService, RepublicPoliceService}
import play.api.libs.ws.ahc.StandaloneAhcWSClient

class ServiceLocator(implicit var system: ActorSystem) {

  private implicit var materializer: Materializer = SystemMaterializer(system).materializer
  val wsClient = StandaloneAhcWSClient()

  val republicIdentificationService: RepublicIdentificationServiceContract = RepublicIdentificationService(wsClient)
  val republicPoliceService: RepublicPoliceServiceContract = RepublicPoliceService(wsClient)

  val prospectRepository: ProspectRepositoryContract = ProspectRepository
  val contactRepository: ContactRepositoryContract = ContactRepository

  val prospectRatingService: ProspectRatingServiceBase = ProspectRatingService
  val validationProspectVsRepublicSystemService = ValidationProspectVsRepublicSystemService(republicIdentificationService, republicPoliceService)
  val prospectProcessingService = ProspectProcessingService(prospectRepository, prospectRatingService, contactRepository, validationProspectVsRepublicSystemService)

}
