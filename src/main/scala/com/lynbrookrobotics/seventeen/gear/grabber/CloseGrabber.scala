package com.lynbrookrobotics.seventeen.gear.grabber

import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class CloseGrabber(grabber: GearGrabber) extends ContinuousTask {
  override protected def onStart(): Unit = {
    grabber.setController(grabber.coreTicks.mapToConstant(GearGrabberClosed))
  }

  override protected def onEnd(): Unit = {
    grabber.resetToDefault()
  }
}
