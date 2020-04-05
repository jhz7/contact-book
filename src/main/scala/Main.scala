import akka.Done
import akka.actor.ActorSystem
import co.com.addi.contactbook.domain.aliases.CustomEither
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

  WebServerStub.startStubServer()
  implicit val system: ActorSystem = ActorSystem()
  val dependencies = new ServiceLocator()

  val tasks =
    ProspectsDataSet.data.values.toList.map(personDto => {
      val dni = Dni(personDto.id, DniCode(personDto.typeId), "")
      dependencies.prospectProcessingService
        .validateProspect(dni).fold( _ => Nil, _ => Nil )
    })

  Await.result( Task.gatherUnordered(tasks).runToFuture, Duration.Inf )

  WebServerStub.stopStubServer()
  println("Finished!!!")

  System.runFinalization()
}
