package com.lynbrookrobotics.seventeen.gear.grabber

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.frc.ProximitySensor
import com.lynbrookrobotics.potassium.tasks.FiniteTask

class OpenGrabberUntilHasGear(implicit hardware: GearGrabberHardware, grabber: GearGrabber,
                              config: GearGrabberConfig) extends FiniteTask {
  val proximitySensor = new ProximitySensor(config.port.proximitySensor)
  override protected def onStart(): Unit = {
    grabber.setController(Signal.constant(GearGrabberOpen).toPeriodic.withCheck { _ =>
      if(proximitySensor.isCloserThan(config.detectingDistance)) {
        finished()
      }
    })
  }

  override protected def onEnd(): Unit = {
    grabber.resetToDefault()
  }
}
