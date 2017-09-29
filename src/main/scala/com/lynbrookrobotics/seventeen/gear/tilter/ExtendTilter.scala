package com.lynbrookrobotics.seventeen.gear.tilter

import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class ExtendTilter(tilter: GearTilter) extends ContinuousTask {
  override protected def onStart(): Unit = {
    tilter.setController(tilter.coreTicks.mapToConstant(GearTilterExtended))
  }

  override protected def onEnd(): Unit = {
    tilter.resetToDefault()
  }
}
