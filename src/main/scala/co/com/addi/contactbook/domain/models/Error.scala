package co.com.addi.contactbook.domain.models

import co.com.addi.contactbook.domain.types.{APPLICATION, BUSINESS, ErrorCode, TECHNICAL}

final case class Error(code: ErrorCode, message: String, exception: Option[Throwable] = None)

case object Error {

  def unifyErrors(errors: List[Error]): Error = {
    val typeErrors = errors.map(_.code)
    val unifiedMessage = errors.map(_.message).mkString(". ")

    (typeErrors.contains(BUSINESS), typeErrors.contains(APPLICATION), typeErrors.contains(TECHNICAL)) match {
      case (true, _, _) => Error(BUSINESS, unifiedMessage)
      case (_, true, _) => Error(APPLICATION, unifiedMessage)
      case (_, _, true) => Error(TECHNICAL, unifiedMessage)
    }
  }
}
