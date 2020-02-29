package co.com.addi.contact.book.application

import akka.actor.ActorSystem
import akka.stream.{Materializer, SystemMaterializer}
import co.com.addi.contact.book.application.services.ProspectRatingService
import co.com.addi.contact.book.domain.contracts.{ContactBaseRepository, ProspectBaseRepository}
import co.com.addi.contact.book.domain.services.{ProspectCriminalRecordValidationService, ProspectDataValidationService, ProspectScoreValidationService}
import co.com.addi.contact.book.infraestructure.repositories.{ContactRepository, ProspectRepository}
import co.com.addi.contact.book.infraestructure.wsclients.{RepublicIdentificationService, RepublicPoliceService}
import play.api.libs.ws.ahc.StandaloneAhcWSClient

trait Dependencies {

  private implicit val system: ActorSystem = ActorSystem()
  private implicit val materializer: Materializer = SystemMaterializer(system).materializer

  val wsClient = StandaloneAhcWSClient()
  val republicIdentificationService: RepublicIdentificationService = RepublicIdentificationService
  val republicPoliceService: RepublicPoliceService = RepublicPoliceService
  val prospectRepository: ProspectBaseRepository = ProspectRepository
  val contactRepository: ContactBaseRepository = ContactRepository
  val prospectRatingService: ProspectRatingService = ProspectRatingService
  val prospectDataValidationService: ProspectDataValidationService = ProspectDataValidationService
  val prospectCriminalRecordValidationService: ProspectCriminalRecordValidationService = ProspectCriminalRecordValidationService
  val prospectScoreValidationService: ProspectScoreValidationService = ProspectScoreValidationService

}
