package co.com.addi.contact.book.application.services

import co.com.addi.contact.book.domain.models.Dni

import scala.util.Random

trait RatingProspectService {

  def rate(dni: Dni): Int

}

object RatingProspectService extends RatingProspectService {

  def rate(dni: Dni): Int =
    Random.nextInt(100) + 1

}
