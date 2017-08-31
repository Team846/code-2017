package com.lynbrookrobotics.seventeen.agitator

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class SpinAgitator(implicit agitator: Agitator, agitatorProperties: Signal[AgitatorProperties]) extends ContinuousTask {
  override def onStart(): Unit = {
    ???
//    agitator.setController(agitatorProperties.map(_.spinSpeed).toPeriodic)
  }

  override def onEnd(): Unit = {
    agitator.resetToDefault()
  }
}
