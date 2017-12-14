package com.lynbrookrobotics.seventeen.gear.roller

import com.lynbrookrobotics.potassium.commons.electronics.CurrentLimiting
import com.lynbrookrobotics.potassium.tasks.{ContinuousTask, FiniteTask}
import squants.Percent
import squants.electro.Amperes
import squants.time.Seconds

class CollectUntilGear(roller: GearRoller) extends FiniteTask {
  var cancelHandle: () => Unit = null
  var ticksAboveLimit = 0

  override protected def onStart(): Unit = {
    roller.disabledAutoRun = false
    val currentStream = roller.coreTicks.map(_ => Amperes(roller.hardware.motor.getOutputCurrent))

    cancelHandle = currentStream.foreach { current =>
      if (current >= roller.properties.get.gearDetectionCurrent) {
        ticksAboveLimit += 1
        if (ticksAboveLimit >= 15) {
          finished()
        }
      } else {
        ticksAboveLimit = 0
      }
    }

    roller.setController(CurrentLimiting.slewRate(
      roller.coreTicks.map(_ => roller.properties.get.intakeGearPower),
      Percent(100) / Seconds(1)
    ))
  }

  override protected def onEnd(): Unit = {
    cancelHandle.apply()
    cancelHandle = null
    ticksAboveLimit = 0
    roller.resetToDefault()
  }
}
