package co.com.addi.contactbook.domain.types

trait DniCode {
  def description: String
}

object DniCode {
  def apply(value: String): DniCode =
    value match {
      case "PA" => Passport
      case "CC" => IdentityCard
    }
}

case object Passport extends DniCode {
  override def description: String = "PA"
}

case object IdentityCard extends DniCode {
  override def description: String = "CC"
}