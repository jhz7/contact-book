package co.com.addi.contact.book.infraestructure.logger

import org.slf4j.LoggerFactory

object Logger {

  private def getLogger[T](`class`: Class[T]) = {
    LoggerFactory.getLogger(`class`)
  }

  def info[T](message: String, `class`: Class[T]): Unit = {
    getLogger(`class`).info(message)
  }

  def error[T](message: String, error: Option[Throwable], `class`: Class[T]): Unit = {
    getLogger(`class`).error(message, error)
  }
}
