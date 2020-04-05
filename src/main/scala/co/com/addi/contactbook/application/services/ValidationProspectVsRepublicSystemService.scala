package co.com.addi.contactbook.application.services

import akka.Done
import cats.data.EitherT
import co.com.addi.contactbook.application.commons.Logging
import co.com.addi.contactbook.domain.aliases.{CustomEither, CustomEitherT}
import co.com.addi.contactbook.domain.contracts.wsclients.{RepublicIdentificationServiceContract, RepublicPoliceServiceContract}
import co.com.addi.contactbook.domain.enhancements.CustomEitherEnhancement._
import co.com.addi.contactbook.domain.models.{Error, Prospect}
import co.com.addi.contactbook.domain.types.{APPLICATION, BUSINESS}
import monix.eval.Task

case class ValidationProspectVsRepublicSystemService(
  republicIdentificationService: RepublicIdentificationServiceContract,
  republicPoliceService: RepublicPoliceServiceContract
) {

  def validate(prospect: Prospect): CustomEitherT[Done] = {
      val validationData = validateProspectData(prospect).value
      val validationCriminalRecord = validateProspectCriminalRecord(prospect).value

      val execution: Task[CustomEither[List[Done]]] =
        Task.gatherUnordered( List(validationData, validationCriminalRecord) ).map(_.group)

      EitherT(execution)map(_ => Done)
  }

  private def validateProspectData(prospect: Prospect): CustomEitherT[Done] = {
      Logging.info(s"Validating personal data for prospect ${prospect.dni.number}...", getClass)

      republicIdentificationService.getProspectData(prospect.dni)
        .subflatMap{
          case Some(prospectData) => prospect.validateDataEquality(prospectData)
          case None               => Left(Error(APPLICATION, s"The prospect ${prospect.dni.number} does not exist in republic identification system"))
        }
  }

  private def validateProspectCriminalRecord(prospect: Prospect): CustomEitherT[Done] = {
      Logging.info(s"Validating criminal record for prospect ${prospect.dni.number}...", getClass)

      republicPoliceService.existsCriminalRecord(prospect.dni)
        .subflatMap{
          case true  => Left(Error(BUSINESS, s"Exists criminal record for the prospect ${prospect.dni.number}"))
          case false => Right(Done)
        }
  }
}
