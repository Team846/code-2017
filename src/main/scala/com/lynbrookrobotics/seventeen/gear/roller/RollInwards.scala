package com.lynbrookrobotics.seventeen.gear.roller

import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class RollInwards(roller: GearRoller) extends ContinuousTask {
  override protected def onStart(): Unit = {
    roller.setController(roller.coreTicks.map(_ => roller.properties.get.intakeGearPower))
  }

  override protected def onEnd(): Unit = {
    roller.resetToDefault()
  }
}
