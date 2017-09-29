package com.lynbrookrobotics.seventeen.collector.extender

import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class ExtendCollector(extender: CollectorExtender) extends ContinuousTask {
  override protected def onStart(): Unit = {
    extender.setController(extender.coreTicks.mapToConstant(CollectorExtenderExtended))
  }

  override protected def onEnd(): Unit = {
    extender.resetToDefault()
  }
}
