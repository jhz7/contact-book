import akka.stream.SystemMaterializer
import co.com.addi.contact.book.application.dtos.PersonDto
import co.com.addi.contact.book.infraestructure.WebServerStub
import play.api.libs.ws.ahc._
import akka.actor.ActorSystem
import akka.stream._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future


object Main extends App {

  WebServerStub.startStubServer()

  val person = PersonDto(id = "1234", typeId = "CC", expeditionIdPlace = "Medellin", firstName = "Jhon", lastName = "Zambrano")

  WebServerStub.mockGetRequest("/prueba", person)

  implicit val system = ActorSystem()
  implicit val materializer = SystemMaterializer(system).materializer
  val wsClient = StandaloneAhcWSClient()

  wsClient
    .url("http://localhost:9001/prueba")
    .get()
    .map(respuesta =>{
      println("Respuesta => ", respuesta.body)
      WebServerStub.stopStubServer()
    })
    .andThen(_ =>{
      wsClient.close()
      system.terminate()
    })

}