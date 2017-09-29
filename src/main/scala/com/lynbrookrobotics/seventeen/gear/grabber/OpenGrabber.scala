package com.lynbrookrobotics.seventeen.gear.grabber

import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class OpenGrabber(grabber: GearGrabber) extends ContinuousTask {
  override protected def onStart(): Unit = {
    grabber.setController(grabber.coreTicks.mapToConstant(GearGrabberOpen))
  }

  override protected def onEnd(): Unit = {
    grabber.resetToDefault()
  }
}
