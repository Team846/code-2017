package com.lynbrookrobotics.seventeen.gear.grabber

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class OpenGrabber(implicit grabber: GearGrabber) extends ContinuousTask {
  override protected def onStart(): Unit = {
    grabber.setController(Signal.constant(GearGrabberOpen).toPeriodic)
  }

  override protected def onEnd(): Unit = {
    grabber.resetToDefault()
  }
}
