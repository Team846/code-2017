package com.lynbrookrobotics.seventeen.collector.extender

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class ExtendCollector(implicit extender: CollectorExtender) extends ContinuousTask {
  override protected def onStart(): Unit = {
    ???
//    extender.setController(Signal.constant(CollectorExtenderExtended).toPeriodic)
  }

  override protected def onEnd(): Unit = {
    extender.resetToDefault()
  }
}
