package co.com.addi.contactbook.application

import akka.actor.ActorSystem
import akka.stream.{Materializer, SystemMaterializer}
import co.com.addi.contactbook.application.services.{ProspectProcessingService, ProspectRatingService}
import co.com.addi.contactbook.domain.contracts.ProspectBaseRepository
import co.com.addi.contactbook.domain.contracts.repositories.{ContactRepositoryContract, ProspectRepositoryContract}
import co.com.addi.contactbook.infraestructure.repositories.{ContactRepository, ProspectRepository}
import co.com.addi.contactbook.infraestructure.wsclients.{RepublicIdentificationService, RepublicPoliceService}
import play.api.libs.ws.ahc.StandaloneAhcWSClient

class Dependencies(implicit val system: ActorSystem) {

  private implicit val materializer: Materializer = SystemMaterializer(system).materializer

  val wsClient = StandaloneAhcWSClient()
  val republicIdentificationService: RepublicIdentificationService = RepublicIdentificationService
  val republicPoliceService: RepublicPoliceService = RepublicPoliceService
  val prospectRepository: ProspectRepositoryContract = ProspectRepository
  val contactRepository: ContactRepositoryContract = ContactRepository
  val prospectRatingService: ProspectRatingService = ProspectRatingService
  val prospectDataValidationService: ProspectDataValidationService = ProspectDataValidationService
  val prospectCriminalRecordValidationService: ProspectCriminalRecordValidationService = ProspectCriminalRecordValidationService
  val prospectScoreValidationService: ProspectScoreValidationService = ProspectScoreValidationService
  val prospectProcessingService: ProspectProcessingService = ProspectProcessingService

}
