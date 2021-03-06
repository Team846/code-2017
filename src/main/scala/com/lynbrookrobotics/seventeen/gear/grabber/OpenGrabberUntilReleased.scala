package com.lynbrookrobotics.seventeen.gear.grabber

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.events.ImpulseEvent
import com.lynbrookrobotics.potassium.tasks.FiniteTask
import com.lynbrookrobotics.seventeen.driver.DriverHardware

class OpenGrabberUntilReleased(implicit hardware: GearGrabberHardware, grabber: GearGrabber,
                               props: Signal[GearGrabberProperties], driverHardware: DriverHardware,
                               polling: ImpulseEvent) extends FiniteTask {
  val proximitySensor = hardware.proximitySensor

  override protected def onStart(): Unit = {
    grabber.setController(grabber.coreTicks.mapToConstant[GearGrabberState](GearGrabberOpen).withCheck { _ =>
      if (proximitySensor.getVoltage < props.get.detectingDistance.toVolts) {
        finished()
      }
    })
  }

  override protected def onEnd(): Unit = {
    grabber.resetToDefault()
  }
}
