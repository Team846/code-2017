package com.lynbrookrobotics.seventeen.climber.extender

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class ExtendClimber(implicit extender: ClimberExtender) extends ContinuousTask {
  override protected def onStart(): Unit = {
    extender.setController(Signal.constant(ClimberExtenderExtended).toPeriodic)
  }

  override protected def onEnd(): Unit = {
    extender.resetToDefault()
  }
}
