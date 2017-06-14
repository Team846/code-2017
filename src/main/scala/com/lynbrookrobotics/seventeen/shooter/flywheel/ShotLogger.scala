package com.lynbrookrobotics.seventeen.shooter.flywheel

import squants.Dimensionless
import squants.time.{Frequency, RevolutionsPerMinute}

import scala.collection.mutable

/**
  * Created by kunal on 6/9/17.
  */
class ShotLogger(val name: String) {

  var sum = RevolutionsPerMinute(0)
  var count = 0

  val record = new mutable.Queue[(Double, Dimensionless)]
  var detected = false

  //  def logError(error: Dimensionless): Unit = {
  //    log.enqueue((Timer.getFPGATimestamp, error))
  //    if (error.abs > Percent(6)) {
  //      detected = true
  //    } else if (error.abs < Percent(1) && detected) {
  //      detected = false
  //      println("----------------------------------------------------------")
  //      val first = log.dequeue()
  //      val min = log.minBy(_._2.value)
  //      val now = log.last
  //      println(s"$name first: @ ${first._1} sec = ${first._2.toPercent.toString.substring(0, 6)}% err")
  //      println(s"$name min: @ ${min._1} sec = ${min._2.toPercent.toString.substring(0, 6)}% err")
  //      println(s"$name last: @ ${now._1} sec = ${now._2.toPercent.toString.substring(0, 6)}% err")
  //      println("----------------------------------------------------------")
  //      log.clear()
  //    } else if (!detected && log.size > 5) {
  //      log.dequeue()
  //    }
  //  }
  def log(velocity: Frequency): Unit = {
    count += 1
    sum += velocity

  }
}
