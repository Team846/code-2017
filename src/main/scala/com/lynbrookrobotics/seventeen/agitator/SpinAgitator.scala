package com.lynbrookrobotics.seventeen.agitator

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class SpinAgitator(agitator: Agitator)(implicit agitatorProperties: Signal[AgitatorProperties]) extends ContinuousTask {
  override def onStart(): Unit = {
    agitator.setController(agitator.coreTicks.map(_ => agitatorProperties.get.spinSpeed))
  }

  override def onEnd(): Unit = {
    agitator.resetToDefault()
  }
}
