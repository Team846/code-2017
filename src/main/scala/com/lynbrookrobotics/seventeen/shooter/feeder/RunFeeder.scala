package com.lynbrookrobotics.seventeen.shooter.feeder

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class RunFeeder(implicit feeder: ShooterFeeder, properties: Signal[ShooterFeederProperties]) extends ContinuousTask {
  override protected def onStart(): Unit = {
    feeder.setController(properties.map(_.feederSpeed).toPeriodic)
  }

  override protected def onEnd(): Unit = {
    feeder.resetToDefault()
  }
}
