package co.com.addi.contactbook

import akka.actor.ActorSystem
import akka.stream.{Materializer, SystemMaterializer}
import co.com.addi.contactbook.infraestructure.FalseServiceLocator
import monix.execution.Scheduler
import monix.execution.Scheduler.Implicits.global
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.specs2.mock.Mockito

trait TestKit extends AnyWordSpec with Matchers with Mockito  with BeforeAndAfterEach {

  val system: ActorSystem = ActorSystem()
  implicit val m: Materializer = SystemMaterializer(system).materializer
  implicit val scheduler: Scheduler = global

  def getFalseServiceLocator: FalseServiceLocator = new FalseServiceLocator()

}
