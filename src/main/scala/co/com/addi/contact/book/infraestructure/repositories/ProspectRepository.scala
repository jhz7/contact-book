package co.com.addi.contact.book.infraestructure.repositories

import cats.data.EitherT
import co.com.addi.contact.book.application.dtos.{ErrorDto, PersonDto}
import co.com.addi.contact.book.application.types.CustomEitherT
import co.com.addi.contact.book.domain.contracts.ProspectBaseRepository
import co.com.addi.contact.book.domain.models.{Dni, Person}
import co.com.addi.contact.book.infraestructure.transformers.PersonTransformer
import monix.eval.Task

object ProspectRepository extends ProspectBaseRepository {

  private val data: Map[String, PersonDto] = Map(
    "1" -> PersonDto(id = "1", typeId = "PA", expeditionIdPlace = "Medell\u00EDn Antioquia", firstName = "Jhon", lastName = "Zambrano"),
    "2" -> PersonDto(id = "2", typeId = "PA", expeditionIdPlace = "Bogot\u00E1", firstName = "Pedro", lastName = "Ortiz"),
    "3" -> PersonDto(id = "3", typeId = "CC", expeditionIdPlace = "Venezuela", firstName = "Maria", lastName = "Maldonado"),
    "4" -> PersonDto(id = "4", typeId = "PA", expeditionIdPlace = "Medell\u00EDn Antioquia", firstName = "Ana", lastName = "Gutierrez"),
    "5" -> PersonDto(id = "5", typeId = "CC", expeditionIdPlace = "Bogot\u00E1", firstName = "Carlos", lastName = "Olaya"),
    "6" -> PersonDto(id = "6", typeId = "CC", expeditionIdPlace = "Venezuela", firstName = "Pablo", lastName = "Berm\u00FAdez"),
    "7" -> PersonDto(id = "7", typeId = "PA", expeditionIdPlace = "Medell\u00EDn Antioquia", firstName = "Andr\u00E9s", lastName = "Carmona"),
    "8" -> PersonDto(id = "8", typeId = "CC", expeditionIdPlace = "Venezuela", firstName = "Orianna", lastName = "Fonseca"),
    "9" -> PersonDto(id = "9", typeId = "CC", expeditionIdPlace = "Bogot\u00E1", firstName = "Alejandra", lastName = "Montoya"),
    "10" -> PersonDto(id = "10", typeId = "PA", expeditionIdPlace = "Medell\u00EDn Antioquia", firstName = "Mart\u00EDn", lastName = "Gutierrez")
  )

  override def get(dni: Dni): CustomEitherT[Option[Person]] = {
    val person: Option[Person] = data.get(dni.number).map(PersonTransformer.toPerson)

    EitherT.rightT[Task, ErrorDto](person)
  }

  override def getAll: CustomEitherT[List[Person]] = {
    val people: List[Person] = data.map(personDto => PersonTransformer.toPerson(personDto._2)).toList

    EitherT.rightT[Task, ErrorDto](people)
  }
}
