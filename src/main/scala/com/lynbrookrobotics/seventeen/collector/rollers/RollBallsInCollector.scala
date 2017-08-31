package com.lynbrookrobotics.seventeen.collector.rollers

import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import squants.Dimensionless

class RollBallsInCollector(rollerSpeedOutput: Stream[Dimensionless])(implicit rollers: CollectorRollers) extends ContinuousTask {
  override protected def onStart(): Unit = {
    rollers.setController(rollerSpeedOutput)
  }

  override protected def onEnd(): Unit = {
    rollers.resetToDefault()
  }
}
