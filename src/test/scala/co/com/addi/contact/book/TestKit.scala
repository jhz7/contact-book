package co.com.addi.contact.book

import akka.actor.ActorSystem
import co.com.addi.contact.book.application.FalseDependencies
import monix.execution.Scheduler
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.specs2.mock.Mockito
import monix.execution.Scheduler.Implicits.global

trait TestKit extends AnyWordSpec with Matchers with Mockito with BeforeAndAfter {

  implicit val system: ActorSystem = ActorSystem()
  implicit val scheduler: Scheduler = global

  def getFalseDependencies: FalseDependencies =
    new FalseDependencies()

}
