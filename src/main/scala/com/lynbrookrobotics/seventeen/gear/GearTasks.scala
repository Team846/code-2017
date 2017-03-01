package com.lynbrookrobotics.seventeen.gear

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.events.ImpulseEvent
import squants.time.Seconds
import com.lynbrookrobotics.potassium.tasks.{FiniteTask, WaitTask}
import com.lynbrookrobotics.seventeen.driver.DriverHardware
import com.lynbrookrobotics.seventeen.gear.tilter.{ExtendTilter, GearTilter, RetractTilter}
import com.lynbrookrobotics.seventeen.gear.grabber._
import com.lynbrookrobotics.potassium.tasks.WaitForContinuousEvent
import com.lynbrookrobotics.potassium.frc.Implicits._

object GearTasks {
  def loadGearFromGroundAbortable(buttonTrigger: Int)
                                 (implicit tilter: GearTilter,
                                  grabber: GearGrabber,
                                  props: Signal[GearGrabberProperties],
                                  hardware: GearGrabberHardware,
                                  clock: Clock,
                                  driverHardware: DriverHardware, polling: ImpulseEvent): FiniteTask = {
    val pickUpGear = new OpenGrabberUntilGearAbortable(buttonTrigger).andUntilDone(
      new ExtendTilter()
    )

    val liftGear = new WaitTask(Seconds(0.3)).andUntilDone(
      new CloseGrabber() and new ExtendTilter()
    ).then(new WaitTask(Seconds(0.3)).andUntilDone(new RetractTilter()))

    pickUpGear.then(liftGear)
  }
}
