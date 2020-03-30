import java.util.concurrent.Executors

import akka.Done
import akka.actor.ActorSystem
import co.com.addi.contactbook.domain.aliases.CustomEither
import co.com.addi.contactbook.domain.models.Dni
import co.com.addi.contactbook.domain.types.DniCode
import co.com.addi.contactbook.infraestructure.ServiceLocator
import co.com.addi.contactbook.infraestructure.datasets.ProspectsDataSet
import co.com.addi.contactbook.infraestructure.webserver.WebServerStub
import monix.eval.Task
import monix.execution.ExecutionModel.AlwaysAsyncExecution
import monix.execution.schedulers.SchedulerService
import monix.execution.{Scheduler, UncaughtExceptionReporter}

object Main extends App {

  implicit val schedulerService: SchedulerService = Scheduler(
    Executors.newFixedThreadPool( 10 ),
    UncaughtExceptionReporter( t => println( s"this should not happen: ${t.getMessage}" ) ),
    AlwaysAsyncExecution
  )

  WebServerStub.startStubServer()
  implicit val system: ActorSystem = ActorSystem()
  val dependencies = new ServiceLocator()

  val tasks: List[Task[CustomEither[Done]]] = ProspectsDataSet.data.values.toList.map(personDto => {
    val dni = Dni(personDto.id, DniCode(personDto.typeId), "")
    dependencies.prospectProcessingService.process(dni).run((dependencies.prospectRepository, dependencies.republicIdentificationService, dependencies.republicPoliceService, dependencies.prospectRatingService, dependencies.contactRepository)).value
  })

  Task.sequence(tasks).runToFuture
    .andThen( _ => {
        WebServerStub.stopStubServer()
        system.terminate()
        schedulerService.shutdown()
      })

}