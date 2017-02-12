package com.lynbrookrobotics.seventeen.collector.rollers

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class RollBallsInCollector(implicit rollers: CollectorRollers, props: Signal[CollectorRollersProperties]) extends ContinuousTask {
  override protected def onStart(): Unit = {
    rollers.setController(props.map(_.rollerSpeed).toPeriodic)
  }

  override protected def onEnd(): Unit = {
    rollers.resetToDefault()
  }
}
