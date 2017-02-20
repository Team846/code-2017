package com.lynbrookrobotics.seventeen.gear

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.frc.ProximitySensor
import squants.time.Seconds
import com.lynbrookrobotics.potassium.tasks.{FiniteTask, WaitTask}
import com.lynbrookrobotics.seventeen.gear.tilter.{ExtendTilter, GearTilter, RetractTilter}
import com.lynbrookrobotics.seventeen.gear.grabber._

object GearTasks {
  def loadGearFromGround(implicit tilter: GearTilter,
                         grabber: GearGrabber, props: Signal[GearGrabberProperties],
                         hardware: GearGrabberHardware, clock: Clock): FiniteTask = {
    val liftGear = new WaitTask(Seconds(0.3)).andUntilDone(
      new CloseGrabber() and new RetractTilter()
    )

    val pickUpGear = new OpenGrabberUntilHasGear().andUntilDone(
      new ExtendTilter()
    )

    pickUpGear then liftGear
  }
}
