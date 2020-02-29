package co.com.addi.contact.book.infraestructure.logger

import org.slf4j.{Logger, LoggerFactory}

object Logging {

  def info[T](message: String, `class`: Class[T]): Unit =
    getLogger(`class`).info(Console.YELLOW + message + Console.RESET)

  def error[T](message: String, throwable: Option[Throwable], `class`: Class[T]): Unit =
    getLogger(`class`).error(Console.RED + message + Console.RESET, throwable)

  private def getLogger[T](`class`: Class[T]): Logger =
    LoggerFactory.getLogger(`class`)
}
