package com.lynbrookrobotics.seventeen.collector.rollers

import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import com.lynbrookrobotics.potassium.clock.Clock
import squants.{Dimensionless, Percent}
import squants.time.Milliseconds

class CollectorRollers(implicit hardware: CollectorRollersHardware, clock: Clock) extends Component[Dimensionless](Milliseconds(5)) {
  override def defaultController: PeriodicSignal[Dimensionless] = Signal.constant(Percent(0)).toPeriodic

  override def applySignal(signal: Dimensionless): Unit = {
    hardware.roller.set(signal.toEach)
  }
}
