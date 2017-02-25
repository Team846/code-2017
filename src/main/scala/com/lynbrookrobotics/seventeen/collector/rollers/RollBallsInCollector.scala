package com.lynbrookrobotics.seventeen.collector.rollers

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import squants.Dimensionless

class RollBallsInCollector(rollerSpeedOutput: Signal[Dimensionless])(implicit rollers: CollectorRollers) extends ContinuousTask {
  override protected def onStart(): Unit = {
    rollers.setController(rollerSpeedOutput.toPeriodic)
  }

  override protected def onEnd(): Unit = {
    rollers.resetToDefault()
  }
}
