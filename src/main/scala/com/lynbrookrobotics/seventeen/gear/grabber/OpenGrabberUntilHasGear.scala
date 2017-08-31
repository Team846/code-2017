package com.lynbrookrobotics.seventeen.gear.grabber

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.FiniteTask

class OpenGrabberUntilHasGear(implicit hardware: GearGrabberHardware, grabber: GearGrabber,
                              props: Signal[GearGrabberProperties]) extends FiniteTask {
  val proximitySensor = hardware.proximitySensor

  override protected def onStart(): Unit = {
    ???
//    grabber.setController(Signal.constant(GearGrabberOpen).toPeriodic.withCheck { _ =>
//      if (proximitySensor.getVoltage > props.get.detectingDistance.toVolts) {
//        finished()
//      }
//    })
  }

  override protected def onEnd(): Unit = {
    grabber.resetToDefault()
  }
}
