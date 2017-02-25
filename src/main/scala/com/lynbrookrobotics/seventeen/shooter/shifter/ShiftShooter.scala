package com.lynbrookrobotics.seventeen.shooter.shifter

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class ShiftShooter(direction: Signal[ShooterShifterState])(implicit shifter: ShooterShifter) extends ContinuousTask {
  override protected def onStart(): Unit = {
    shifter.setController(direction.toPeriodic)
  }

  override protected def onEnd(): Unit = {
    shifter.resetToDefault()
  }
}
