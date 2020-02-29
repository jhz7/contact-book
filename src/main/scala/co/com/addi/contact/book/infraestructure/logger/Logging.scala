package co.com.addi.contact.book.infraestructure.logger


object Logging {

  def info[T](message: String, `class`: Class[T]): Unit = {
//    getLoggerX(`class`).info(message)
    println("INFO: " + message + " " + `class`.getName )
  }

  def error[T](message: String, error: Option[Throwable], `class`: Class[T]): Unit = {
//    getLoggerX(`class`).error(message, error)
    println("ERROR: " + message + " " + `class`.getName )
  }

  private def getLoggerX[T](`class`: Class[T]) = {
//    LoggerFactory.getLogger(`class`)
  }
}
