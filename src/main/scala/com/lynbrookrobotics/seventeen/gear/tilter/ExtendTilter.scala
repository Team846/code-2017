package com.lynbrookrobotics.seventeen.gear.tilter

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.potassium.streams.Stream
import squants.time.Seconds

class ExtendTilter(implicit tilter: GearTilter) extends ContinuousTask {
  override protected def onStart(): Unit = {
    tilter.setController(tilter.coreTicks.mapToConstant(GearTilterExtended))
  }

  override protected def onEnd(): Unit = {
    tilter.resetToDefault()
  }
}
