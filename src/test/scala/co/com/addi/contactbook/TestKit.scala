package co.com.addi.contactbook

import akka.actor.ActorSystem
import co.com.addi.contactbook.application.FalseDependencies
import monix.execution.Scheduler
import monix.execution.Scheduler.Implicits.global
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.specs2.mock.Mockito

trait TestKit extends AnyWordSpec with Matchers with Mockito  with BeforeAndAfterEach {

  implicit val system: ActorSystem = ActorSystem()
  implicit val scheduler: Scheduler = global

  def getFalseDependencies: FalseDependencies =
    new FalseDependencies()

}
