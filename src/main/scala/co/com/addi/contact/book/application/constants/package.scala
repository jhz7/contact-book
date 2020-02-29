package co.com.addi.contact.book.application

package object constants {

  val idRepublicWebServerUrl: String = "/id-republic-service/person/info"

  def errorMessageGettingPerson(id: String) = s"Has occurred an error getting info for person with id $id"

  def errorMessageGettingCriminalRecord(id: String) = s"Has occurred an error getting criminal record for person with id $id"

}
