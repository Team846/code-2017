package com.lynbrookrobotics.seventeen.collector.rollers

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.electronics.CurrentLimiting
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import com.lynbrookrobotics.potassium.streams.Stream
import squants.time.{Milliseconds, Seconds}
import squants.{Dimensionless, Percent}

class CollectorRollers(implicit hardware: CollectorRollersHardware, clock: Clock) extends Component[Dimensionless](Milliseconds(5)) {
  override def defaultController: Stream[Dimensionless] = Stream.periodic(Seconds(0.01)) {
    Percent(0)
  }

  override def setController(controller: Stream[Dimensionless]): Unit = {
    super.setController(CurrentLimiting.slewRate(controller, Percent(100) / Seconds(0.3)))
  }

  override def applySignal(signal: Dimensionless): Unit = {
    hardware.roller.set(signal.toEach)
  }
}
