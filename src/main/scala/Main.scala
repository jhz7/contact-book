import akka.actor.ActorSystem
import akka.stream.{Materializer, SystemMaterializer}
import co.com.addi.contactbook.domain.models.Dni
import co.com.addi.contactbook.domain.types.DniCode
import co.com.addi.contactbook.infraestructure.ServiceLocator
import co.com.addi.contactbook.infraestructure.datasets.ProspectsDataSet
import co.com.addi.contactbook.infraestructure.webserver.WebServerStub
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object Main extends App {

  WebServerStub.startStubServer()

  val system: ActorSystem = ActorSystem()
  implicit var m: Materializer = SystemMaterializer(system).materializer

  val dependencies = new ServiceLocator()

  val tasks =
    ProspectsDataSet.data.values.toList.map(personDto => {
      val dni = Dni(personDto.id, DniCode(personDto.typeId), "")
      dependencies.prospectProcessingService
        .becomeContact(dni).fold(_ => Nil, _ => Nil )
    })

  Await.result(
    Task.sequence(tasks).runToFuture.flatMap( _ => finishProcess()), Duration.Inf )

//  System.runFinalization()
//  System.exit(1)

  def finishProcess() = {
    WebServerStub.stopStubServer()
    m.shutdown()
    system.terminate()
  }
}
