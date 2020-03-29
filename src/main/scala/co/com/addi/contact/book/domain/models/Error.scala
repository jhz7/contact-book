package co.com.addi.contact.book.domain.models

import co.com.addi.contact.book.domain.types.ErrorCode

final case class Error(code: ErrorCode, message: String, exception: Option[Throwable] = None)
