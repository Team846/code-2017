package com.lynbrookrobotics.seventeen.climber.puller

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import squants.time.Milliseconds
import squants.{Dimensionless, Percent}

class ClimberPuller(implicit hardware: ClimberPullerHardware, clock: Clock) extends Component[Dimensionless](Milliseconds(5)) {
  override def defaultController: PeriodicSignal[Dimensionless] = Signal.constant(Percent(0)).toPeriodic

  override def applySignal(signal: Dimensionless): Unit = {
    hardware.motorA.set(signal.toEach)
    hardware.motorB.set(signal.toEach)
  }
}
