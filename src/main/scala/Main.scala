import akka.actor.ActorSystem
import akka.stream.{Materializer, SystemMaterializer}
import co.com.addi.contactbook.domain.models.Dni
import co.com.addi.contactbook.domain.types.DniCode
import co.com.addi.contactbook.infraestructure.ServiceLocator
import co.com.addi.contactbook.infraestructure.datasets.ProspectsDataSet
import co.com.addi.contactbook.infraestructure.webserver.WebServerStub
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App {

  val system: ActorSystem = ActorSystem()
  implicit var m: Materializer = SystemMaterializer(system).materializer
  WebServerStub.startStubServer()

  val dependencies = new ServiceLocator()

  def getProcesses =
    ProspectsDataSet.data.values.toList.map(personDto =>
      dependencies.prospectProcessingService.becomeContact(Dni(personDto.id, DniCode(personDto.typeId), personDto.expeditionIdPlace)).value
    )

  def finishProcess = {
    WebServerStub.stopStubServer()
    m.shutdown()
    system.terminate()
  }

  def execute = {
    Await.result(
      Task.sequence( getProcesses ).runToFuture
        .flatMap( _ => finishProcess)
      , Duration.Inf
    )
  }

  execute
}
