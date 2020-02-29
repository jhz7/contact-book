package co.com.addi.contact.book.domain.models

trait DniCode

object DniCode {
  def apply(value: String): DniCode =
    value match {
      case "PA" => Passport
      case "CC" => IdentityCard
    }
}

object Passport extends DniCode
object IdentityCard extends DniCode

final case class Dni(number: String, code: DniCode)
