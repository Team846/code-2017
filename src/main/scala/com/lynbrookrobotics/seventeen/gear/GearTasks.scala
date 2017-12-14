package com.lynbrookrobotics.seventeen.gear

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.events.ImpulseEvent
import com.lynbrookrobotics.potassium.tasks.{ContinuousTask, FiniteTask, WaitTask}
import com.lynbrookrobotics.seventeen.driver.DriverHardware
import com.lynbrookrobotics.seventeen.gear.roller._
import com.lynbrookrobotics.seventeen.gear.tilter.{ExtendTilter, GearTilter, RetractTilter}
import squants.time.Seconds

object GearTasks {
  def collectGear(tilter: GearTilter, roller: GearRoller)
                 (implicit clock: Clock): FiniteTask = {
    new CollectUntilGear(roller).andUntilDone(new ExtendTilter(tilter))
  }

  def scoreGear(tilter: GearTilter, roller: GearRoller)
               (implicit clock: Clock): ContinuousTask = {
    new RollOutwards(roller) and new ExtendTilter(tilter)
  }
}
