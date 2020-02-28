package co.com.addi.contact.book.application.dtos


final case class ErrorDto (`type`: KindError, message: String, exception: Option[Throwable] = None)

trait KindError
case object BUSINESS extends KindError
case object TECHNICAL extends KindError
case object APPLICATION extends KindError
