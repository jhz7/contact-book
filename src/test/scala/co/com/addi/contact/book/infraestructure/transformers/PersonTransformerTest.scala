package co.com.addi.contact.book.infraestructure.transformers

import co.com.addi.contact.book.domain.models.DniCode
import co.com.addi.contact.book.factories.PersonFactory
import com.softwaremill.quicklens.ModifyPimp
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PersonTransformerTest extends AnyWordSpec with Matchers {

  "PersonTransformer" should {

    "Transform PersonDto to Person model" in {
      val firstName = "Jhon"
      val lastName = "Zambrano"
      val id = "1234567890"
      val idType = "CC"
      val expeditionIdPlace = "Medell\u00EDn"

      val expectedDni = PersonFactory.createDni
        .modify(_.number).setTo(id)
        .modify(_.code).setTo(DniCode(idType))
      val personDto = PersonFactory
        .createPersonDto
        .modify(_.id).setTo(id)
        .modify(_.typeId).setTo(idType)
        .modify(_.firstName).setTo(firstName)
        .modify(_.lastName).setTo(lastName)
        .modify(_.expeditionIdPlace).setTo(expeditionIdPlace)

      val result = PersonTransformer.toPerson(personDto)

      result.dni mustBe expectedDni
      result.firstName mustBe firstName
      result.lastName mustBe lastName
      result.expeditionIdPlace mustBe expeditionIdPlace
    }
  }
}
