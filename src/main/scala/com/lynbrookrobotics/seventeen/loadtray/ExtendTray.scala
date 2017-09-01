package com.lynbrookrobotics.seventeen.loadtray

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.potassium.streams.Stream
import squants.time.Seconds

class ExtendTray(implicit extender: LoadTray, clock: Clock) extends ContinuousTask {
  override protected def onStart(): Unit = {
    extender.setController(Stream.periodic(Seconds(0.01))(LoadTrayExtended))
  }

  override protected def onEnd(): Unit = {
    extender.resetToDefault()
  }
}
