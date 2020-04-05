package co.com.addi.contactbook.infraestructure.transformers

import co.com.addi.contactbook.domain.types.DniCode
import co.com.addi.contactbook.factories.PersonFactory
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

      val personDto = PersonFactory
        .createPersonDto
        .modify(_.id).setTo(id)
        .modify(_.typeId).setTo(idType)
        .modify(_.firstName).setTo(firstName)
        .modify(_.lastName).setTo(lastName)
        .modify(_.expeditionIdPlace).setTo(expeditionIdPlace)

      val result = PersonTransformer.toProspect(personDto)

      result.dni.number mustBe id
      result.dni.code mustBe DniCode(idType)
      result.dni.expeditionPlace mustBe expeditionIdPlace
      result.firstName mustBe firstName
      result.lastName mustBe lastName
    }
  }
}
