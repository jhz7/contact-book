package co.com.addi.contactbook.application.services

import co.com.addi.contactbook.domain.models.Dni

import scala.util.Random

trait ProspectRatingServiceBase {

  def rate(dni: Dni): Int = Random.nextInt(100) + 1

}

object ProspectRatingService extends ProspectRatingServiceBase
