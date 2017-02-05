package com.lynbrookrobotics.seventeen.collector.elevator

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, Signal}
import squants.{Dimensionless, Percent}
import squants.time.Milliseconds

class CollectorElevator(implicit hardware: CollectorElevatorHardware, clock: Clock)
  extends Component[Dimensionless](Milliseconds(5)) {
  override def defaultController = Signal.constant(Percent(0)).toPeriodic

  override def applySignal(signal: Dimensionless): Unit = {
    hardware.motor.set(signal.toEach)
  }
}
