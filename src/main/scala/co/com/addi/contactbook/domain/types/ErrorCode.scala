package co.com.addi.contactbook.domain.types

sealed trait ErrorCode

case object BUSINESS extends ErrorCode
case object TECHNICAL extends ErrorCode
case object APPLICATION extends ErrorCode
