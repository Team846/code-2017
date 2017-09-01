package com.lynbrookrobotics.seventeen.gear.grabber

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.potassium.streams.Stream
import squants.time.Seconds

class CloseGrabber(implicit grabber: GearGrabber, clock: Clock) extends ContinuousTask {
  override protected def onStart(): Unit = {
    grabber.setController(Stream.periodic(Seconds(0.01))(GearGrabberClosed))
  }

  override protected def onEnd(): Unit = {
    grabber.resetToDefault()
  }
}
