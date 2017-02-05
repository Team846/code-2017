package com.lynbrookrobotics.seventeen.vision

import akka.actor._
import com.lynbrookrobotics.potassium.clock._
import com.lynbrookrobotics.potassium.remote._
import com.lynbrookrobotics.potassium.vision._
import org.opencv.videoio._

import squants.Time
import squants.time.Milliseconds

object RemoteMain extends App {
  override def main(args: Array[String]) = {
    clock.apply(Milliseconds(50))((x: Time) => {
      println("Time: " + x)
    })
  }
}
