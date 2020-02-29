import java.util.concurrent.Executors

import akka.Done
import akka.actor.ActorSystem
import co.com.addi.contact.book.application.Dependencies
import co.com.addi.contact.book.application.dtos.ErrorDto
import co.com.addi.contact.book.application.types.CustomEither
import co.com.addi.contact.book.domain.models.{Dni, DniCode, IdentityCard}
import co.com.addi.contact.book.infraestructure.databases.ProspectsDataBase
import co.com.addi.contact.book.infraestructure.webserver.WebServerStub
import monix.eval.Task
import monix.execution.ExecutionModel.AlwaysAsyncExecution
import monix.execution.UncaughtExceptionReporter
import monix.execution.schedulers.ExecutorScheduler


object Main extends App {

  implicit val commandSchedulerTest: ExecutorScheduler = ExecutorScheduler(
    Executors.newFixedThreadPool( 10 ),
    UncaughtExceptionReporter( t => println( s"this should not happen: ${t.getMessage}" ) ),
    AlwaysAsyncExecution
  )

  WebServerStub.startStubServer()
  implicit val system: ActorSystem = ActorSystem()
  val dependencies = new Dependencies()

  val tasks: List[Task[CustomEither[Done]]] = ProspectsDataBase.data.values.toList.map(personDto => {
    val dni = Dni(personDto.id, DniCode(personDto.typeId))
    dependencies.prospectProcessingService.process(dni).run(dependencies).value
  })

  Task.sequence(tasks).runToFuture
    .andThen(_ => {
        WebServerStub.stopStubServer()
        system.terminate()
      })

}