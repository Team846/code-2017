package com.lynbrookrobotics.seventeen.gear.roller

import com.lynbrookrobotics.potassium.tasks.{ContinuousTask, FiniteTask}
import squants.electro.Amperes

class CollectUntilGear(roller: GearRoller) extends FiniteTask {
  override protected def onStart(): Unit = {
    roller.setController(roller.coreTicks.map(_ => roller.properties.get.intakeGearPower).withCheck { _ =>
      if (Amperes(roller.hardware.motor.getOutputCurrent) >= roller.properties.get.gearDetectionCurrent) {
        finished()
      }
    })
  }

  override protected def onEnd(): Unit = {
    roller.resetToDefault()
  }
}
