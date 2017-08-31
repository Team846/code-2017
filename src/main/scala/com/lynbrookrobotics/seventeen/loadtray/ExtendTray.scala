package com.lynbrookrobotics.seventeen.loadtray

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class ExtendTray(implicit extender: LoadTray) extends ContinuousTask {
  override protected def onStart(): Unit = {
    ???
//    extender.setController(Signal.constant(LoadTrayExtended).toPeriodic)
  }

  override protected def onEnd(): Unit = {
    extender.resetToDefault()
  }
}
