package com.lynbrookrobotics.seventeen.gear.tilter

import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class RetractTilter(tilter: GearTilter) extends ContinuousTask {
  override protected def onStart(): Unit = {
    tilter.setController(tilter.coreTicks.mapToConstant(GearTilterRetracted))
  }

  override protected def onEnd(): Unit = {
    tilter.resetToDefault()
  }
}
