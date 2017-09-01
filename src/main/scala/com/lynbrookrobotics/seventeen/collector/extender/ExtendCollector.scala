package com.lynbrookrobotics.seventeen.collector.extender

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.potassium.streams.Stream
import squants.time.Seconds

class ExtendCollector(implicit extender: CollectorExtender, clock: Clock) extends ContinuousTask {
  override protected def onStart(): Unit = {
    extender.setController(Stream.periodic(Seconds(0.01))(CollectorExtenderExtended))
  }

  override protected def onEnd(): Unit = {
    extender.resetToDefault()
  }
}
