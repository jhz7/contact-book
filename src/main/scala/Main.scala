import akka.actor.ActorSystem
import akka.stream.{Materializer, SystemMaterializer}
import co.com.addi.contact.book.application.dtos.PersonDto
import co.com.addi.contact.book.infraestructure.WebServerStub
import play.api.libs.ws.ahc._

import scala.concurrent.ExecutionContext.Implicits._


object Main extends App {

  WebServerStub.startStubServer()

  val person = PersonDto(id = "1234", typeId = "CC", expeditionIdPlace = "Medellin", firstName = "Jhon", lastName = "Zambrano")

  WebServerStub.mockSuccessNoContentGetRequest("/prueba")

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = SystemMaterializer(system).materializer
  val wsClient = StandaloneAhcWSClient()

  wsClient
    .url("http://localhost:9001/prueba")
    .get()
    .map(respuesta => println("Respuesta => ", respuesta.body))
    .andThen(_ =>{
      wsClient.close()
      WebServerStub.stopStubServer()
      system.terminate()
    })

}