package co.com.addi.contact.book.application.commons

import org.slf4j.{Logger, LoggerFactory}

object Logging {

  def info[T](message: String, clazz: Class[T]): Unit =
    getLogger(clazz).info(Console.GREEN + message + Console.RESET)

  def warn[T](message: String, clazz: Class[T]): Unit =
    getLogger(clazz).info(Console.YELLOW + message + Console.RESET)

  def error[T](message: String, clazz: Class[T], throwable: Option[Throwable] = None): Unit =
    getLogger(clazz).error(Console.RED + message + Console.RESET, throwable)

  private def getLogger[T](clazz: Class[T]): Logger = LoggerFactory.getLogger(clazz)

}
