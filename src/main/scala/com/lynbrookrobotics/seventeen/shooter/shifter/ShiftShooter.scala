package com.lynbrookrobotics.seventeen.shooter.shifter

import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class ShiftShooter(direction: Stream[ShooterShifterState])(shifter: ShooterShifter) extends ContinuousTask {
  override protected def onStart(): Unit = {
    shifter.setController(direction)
  }

  override protected def onEnd(): Unit = {
    shifter.resetToDefault()
  }
}
