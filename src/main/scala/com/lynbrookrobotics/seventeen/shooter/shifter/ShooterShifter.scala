package com.lynbrookrobotics.seventeen.shooter.shifter

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import squants.time.Milliseconds

sealed trait ShooterShifterState
case object ShooterShiftLeft extends ShooterShifterState
case object ShooterShiftRight extends ShooterShifterState

class ShooterShifter(implicit hardware: ShooterShifterHardware, clock: Clock) extends Component[ShooterShifterState](Milliseconds(5)) {
  var currentState: ShooterShifterState = ShooterShiftRight

  override def defaultController: PeriodicSignal[ShooterShifterState] = Signal(currentState).toPeriodic

  override def applySignal(signal: ShooterShifterState): Unit = {
    hardware.pneumatic.set(signal == ShooterShiftLeft)
  }
}
