package com.lynbrookrobotics.seventeen.gear.grabber

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.events.ImpulseEvent
import com.lynbrookrobotics.potassium.frc.Implicits._
import com.lynbrookrobotics.potassium.tasks.FiniteTask
import com.lynbrookrobotics.seventeen.driver.DriverHardware

class OpenGrabberUntilReleased(implicit hardware: GearGrabberHardware, grabber: GearGrabber,
                               props: Signal[GearGrabberProperties], driverHardware: DriverHardware,
                               polling: ImpulseEvent) extends FiniteTask {

  val proximitySensor = hardware.proximitySensor
  override protected def onStart(): Unit = {
    grabber.setController(Signal.constant(GearGrabberOpen).toPeriodic.withCheck { _ =>
      if (!proximitySensor.isCloserThan(props.get.detectingDistance)) {
        finished()
      }
    })
  }

  override protected def onEnd(): Unit = {
    grabber.resetToDefault()
  }
}
