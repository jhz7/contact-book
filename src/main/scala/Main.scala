import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.stream.{Materializer, SystemMaterializer}
import co.com.addi.contact.book.application.Dependencies
import co.com.addi.contact.book.application.dtos.PersonDto
import co.com.addi.contact.book.domain.models.{Dni, IdentityCard}
import co.com.addi.contact.book.infraestructure.webserver.WebServerStub
import monix.execution.ExecutionModel.AlwaysAsyncExecution
import monix.execution.UncaughtExceptionReporter
import monix.execution.schedulers.ExecutorScheduler
import play.api.libs.ws.ahc._

import scala.concurrent.ExecutionContext.Implicits._


object Main extends App {

//  WebServerStub.startStubServer()
//
//  val person = PersonDto(id = "1234", typeId = "CC", expeditionIdPlace = "Medellin", firstName = "Jhon", lastName = "Zambrano")
//
//  WebServerStub.mockSuccessNoContentGetRequest("/prueba")
//
//  implicit val system: ActorSystem = ActorSystem()
//  implicit val materializer: Materializer = SystemMaterializer(system).materializer
//  val wsClient = StandaloneAhcWSClient()
//
//  wsClient
//    .url("http://localhost:9001/prueba")
//    .get()
//    .map(respuesta => println("Respuesta => ", respuesta.body))
//    .andThen(_ =>{
//      wsClient.close()
//      WebServerStub.stopStubServer()
//      system.terminate()
//    })

  implicit val commandSchedulerTest: ExecutorScheduler = ExecutorScheduler(
    Executors.newFixedThreadPool( 10 ),
    UncaughtExceptionReporter( t => println( s"this should not happen: ${t.getMessage}" ) ),
    AlwaysAsyncExecution
  )

  WebServerStub.startStubServer()

  val dni = Dni(number = "1", code = IdentityCard)

  implicit val system: ActorSystem = ActorSystem()

  val dependencies = new Dependencies()

  dependencies.prospectProcessingService
    .process(dni).run(dependencies).value.runToFuture
    .andThen(_ => {
      WebServerStub.stopStubServer()
      system.terminate()
    })

}