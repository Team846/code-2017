package com.lynbrookrobotics.seventeen.gear.collector

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class ExtendCollector(implicit collector: GearCollector) extends ContinuousTask {
  override protected def onStart(): Unit = {
    collector.setController(Signal.constant(GearCollectorExtended).toPeriodic)
  }

  override protected def onEnd(): Unit = {
    collector.setController(Signal.constant(GearCollectorRetracted).toPeriodic)
  }
}
