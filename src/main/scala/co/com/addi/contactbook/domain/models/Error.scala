package co.com.addi.contactbook.domain.models

import co.com.addi.contactbook.domain.types.ErrorCode

final case class Error(code: ErrorCode, message: String, exception: Option[Throwable] = None)
