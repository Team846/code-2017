package com.lynbrookrobotics.seventeen.shooter.flywheel

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import squants.time.Milliseconds
import squants.{Dimensionless, Percent}

class ShooterFlywheel(implicit hardware: ShooterFlywheelHardware, clock: Clock)
  extends Component[DoubleFlywheelSignal](Milliseconds(5)) {
  override def defaultController = Signal.constant(
    DoubleFlywheelSignal(Percent(0), Percent(0))
  ).toPeriodic

  override def applySignal(signal: DoubleFlywheelSignal): Unit = {
    hardware.leftMotor.set(signal.left.toEach)
    hardware.rightMotor.set(signal.right.toEach)
  }
}
