package com.lynbrookrobotics.seventeen.gear.roller

import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class RollOutwards(roller: GearRoller) extends ContinuousTask {
  override protected def onStart(): Unit = {
    roller.disabledAutoRun = false
    roller.setController(roller.coreTicks.map(_ => roller.properties.get.emitGearPower))
  }

  override protected def onEnd(): Unit = {
    roller.resetToDefault()
  }
}
