package com.lynbrookrobotics.seventeen.agitator

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class SpinAgitator(implicit agitator: Agitator) extends ContinuousTask {
  override def onStart(): Unit = {
    agitator.setController(Signal.constant(AgitatorSpinning).toPeriodic)
  }

  override def onEnd(): Unit = {
    agitator.resetToDefault()
  }
}
