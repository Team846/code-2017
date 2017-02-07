package com.lynbrookrobotics.seventeen.collector.rollers

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class RollBallsInCollector(implicit rollers: CollectorRollers, config: CollectorRollersConfig) extends ContinuousTask {
  override protected def onStart(): Unit = {
    rollers.setController(Signal.constant(config.rollerSpeed).toPeriodic)
  }

  override protected def onEnd(): Unit = {
    rollers.resetToDefault()
  }
}
