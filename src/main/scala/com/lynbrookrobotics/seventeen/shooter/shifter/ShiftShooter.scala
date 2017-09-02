package com.lynbrookrobotics.seventeen.shooter.shifter

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.potassium.streams.Stream

class ShiftShooter(direction: Stream[ShooterShifterState])(implicit shifter: ShooterShifter) extends ContinuousTask {
  override protected def onStart(): Unit = {
    shifter.setController(direction)
  }

  override protected def onEnd(): Unit = {
    shifter.resetToDefault()
  }
}
