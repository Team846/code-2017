package com.lynbrookrobotics.seventeen.gear.tilter

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.potassium.streams.Stream
import squants.time.Seconds

class RetractTilter(implicit tilter: GearTilter, clock: Clock) extends ContinuousTask {
  override protected def onStart(): Unit = {
    tilter.setController(Stream.periodic(Seconds(0.5))(GearTilterRetracted))
  }

  override protected def onEnd(): Unit = {
    tilter.resetToDefault()
  }
}
