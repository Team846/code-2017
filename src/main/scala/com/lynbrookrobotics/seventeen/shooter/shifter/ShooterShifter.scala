package com.lynbrookrobotics.seventeen.shooter.shifter

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.Component
import squants.time.{Milliseconds, Seconds}

sealed trait ShooterShifterState

case object ShooterShiftLeft extends ShooterShifterState

case object ShooterShiftRight extends ShooterShifterState

class ShooterShifter(implicit hardware: ShooterShifterHardware, clock: Clock) extends Component[ShooterShifterState](Milliseconds(5)) {
  var currentState: ShooterShifterState = ShooterShiftRight

  override def defaultController: Stream[ShooterShifterState] = {
    Stream.periodic(Seconds(0.1)) {
      currentState
    }
  }

  override def applySignal(signal: ShooterShifterState): Unit = {
    hardware.pneumatic.set(signal == ShooterShiftLeft)
  }
}
