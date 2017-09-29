package com.lynbrookrobotics.seventeen.gear

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.events.ImpulseEvent
import com.lynbrookrobotics.potassium.tasks.{FiniteTask, WaitTask}
import com.lynbrookrobotics.seventeen.driver.DriverHardware
import com.lynbrookrobotics.seventeen.gear.grabber._
import com.lynbrookrobotics.seventeen.gear.tilter.{ExtendTilter, GearTilter, RetractTilter}
import squants.time.Seconds

object GearTasks {
  def loadGearFromGroundAbortable(buttonTrigger: Int)
                                 (tilter: GearTilter,
                                  grabber: GearGrabber)
                                 (implicit props: Signal[GearGrabberProperties],
                                  hardware: GearGrabberHardware,
                                  clock: Clock,
                                  driverHardware: DriverHardware, polling: ImpulseEvent): FiniteTask = {
    val pickUpGear = new OpenGrabberUntilGearAbortable(buttonTrigger)(grabber).andUntilDone(
      new ExtendTilter(tilter)
    )

    val liftGear = new WaitTask(Seconds(0.3)).andUntilDone(
      new CloseGrabber(grabber) and new ExtendTilter(tilter)
    ).then(new WaitTask(Seconds(0.3)).andUntilDone(new RetractTilter(tilter)))

    pickUpGear.then(liftGear)
  }

  def slamDunk(tilter: GearTilter,
               grabber: GearGrabber)
              (implicit props: Signal[GearGrabberProperties],
               hardware: GearGrabberHardware,
               clock: Clock,
               driverHardware: DriverHardware, polling: ImpulseEvent): FiniteTask = {
    val pickUpGear = new WaitTask(Seconds(1)).andUntilDone(
      new ExtendTilter(tilter) and new OpenGrabber(grabber)
    )

    val liftGear = new WaitTask(Seconds(0.3)).andUntilDone(
      new CloseGrabber(grabber) and new ExtendTilter(tilter)
    ).then(new WaitTask(Seconds(0.3)).andUntilDone(new RetractTilter(tilter)))

    pickUpGear.then(liftGear)
  }
}
