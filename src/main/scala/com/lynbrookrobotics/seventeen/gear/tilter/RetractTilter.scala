package com.lynbrookrobotics.seventeen.gear.tilter

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class RetractTilter(implicit tilter: GearTilter) extends ContinuousTask {
  override protected def onStart(): Unit = {
    tilter.setController(Signal.constant(GearTilterRetracted).toPeriodic)
  }

  override protected def onEnd(): Unit = {
    tilter.resetToDefault()
  }
}
