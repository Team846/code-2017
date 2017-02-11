package com.lynbrookrobotics.seventeen.gear

import com.lynbrookrobotics.potassium.clock.Clock
import squants.time.Seconds
import com.lynbrookrobotics.potassium.tasks.WaitTask

import com.lynbrookrobotics.seventeen.gear.tilter.{GearTilter, ExtendTilter, RetractTilter}
import com.lynbrookrobotics.seventeen.gear.grabber.{GearGrabber, OpenGrabber, CloseGrabber}

object GearTasks {
  def loadGearFromGround(implicit tilter: GearTilter,
                         grabber: GearGrabber,
                         clock: Clock) = {
    val expandGrabber = new WaitTask(Seconds(0.1)).andUntilDone(
      new OpenGrabber()
    )

    val dropToGround = new WaitTask(Seconds(0.3)).andUntilDone(
      new OpenGrabber() and new ExtendTilter()
    )

    val grabGear = new WaitTask(Seconds(0.3)).andUntilDone(
      new CloseGrabber() and new ExtendTilter()
    )

    val liftGear = new WaitTask(Seconds(0.3)).andUntilDone(
      new CloseGrabber() and new RetractTilter()
    )

    expandGrabber then dropToGround then grabGear then liftGear
  }
}
