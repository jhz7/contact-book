package co.com.addi.contact.book.application

import akka.actor.ActorSystem
import akka.stream.{Materializer, SystemMaterializer}
import co.com.addi.contact.book.infraestructure.wsclients.{IdRepublicService, PoliceRepublicService}
import play.api.libs.ws.ahc.StandaloneAhcWSClient

object Dependencies {

  private implicit val system: ActorSystem = ActorSystem()
  private implicit val materializer: Materializer = SystemMaterializer(system).materializer

  val wsClient = StandaloneAhcWSClient()
  val idRepublicService: IdRepublicService = IdRepublicService
  val policeRepublicService: PoliceRepublicService = PoliceRepublicService
}
