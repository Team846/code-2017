package com.lynbrookrobotics.seventeen.collector.rollers

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.electronics.CurrentLimiting
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import squants.time.{Milliseconds, Seconds}
import squants.{Dimensionless, Percent}

class CollectorRollers(implicit hardware: CollectorRollersHardware, clock: Clock) extends Component[Dimensionless](Milliseconds(5)) {
  override def defaultController: PeriodicSignal[Dimensionless] = Signal.constant(Percent(0)).toPeriodic

  override def setController(controller: PeriodicSignal[Dimensionless]): Unit = {
    super.setController(CurrentLimiting.slewRate(controller, Percent(100) / Seconds(0.3)))
  }

  override def applySignal(signal: Dimensionless): Unit = {
    hardware.roller.set(signal.toEach)
  }
}
