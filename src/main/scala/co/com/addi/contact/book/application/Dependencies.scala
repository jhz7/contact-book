package co.com.addi.contact.book.application

import akka.actor.ActorSystem
import akka.stream.{Materializer, SystemMaterializer}
import co.com.addi.contact.book.domain.contracts.{ContactBaseRepository, ProspectBaseRepository}
import co.com.addi.contact.book.infraestructure.repositories.{ContactRepository, ProspectRepository}
import co.com.addi.contact.book.infraestructure.wsclients.{RepublicIdentificationService, RepublicPoliceService}
import play.api.libs.ws.ahc.StandaloneAhcWSClient

object Dependencies {

  private implicit val system: ActorSystem = ActorSystem()
  private implicit val materializer: Materializer = SystemMaterializer(system).materializer

  val wsClient = StandaloneAhcWSClient()
  val idRepublicService: RepublicIdentificationService = RepublicIdentificationService
  val policeRepublicService: RepublicPoliceService = RepublicPoliceService
  val prospectRepository: ProspectBaseRepository = ProspectRepository
  val contactRepository: ContactBaseRepository = ContactRepository

}
