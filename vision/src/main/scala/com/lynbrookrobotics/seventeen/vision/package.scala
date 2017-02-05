package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.clock._

import squants.Time
import squants.time.Milliseconds

package object vision {
  implicit val clock: Clock = JavaClock
  val updatePeriod: Time = Milliseconds(5)
}
