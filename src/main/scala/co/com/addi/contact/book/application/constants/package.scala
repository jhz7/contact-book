package co.com.addi.contact.book.application

package object constants {

  val idRepublicWebServerUrl: String = "/id-republic-service/person/info"

  def errorMessageGettingCriminalRecord(id: String) = s"Has occurred an error getting criminal record for person with id $id"

}
