package co.com.addi.contact.book.tools

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object FutureTool {

  def awaitResult[T](future: Future[T]): T =
    Await.result(future, Duration.Inf)

}
