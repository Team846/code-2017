package com.lynbrookrobotics.seventeen.collector.elevator

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class LoadIntoStorage(implicit elevator: CollectorElevator, config: CollectorElevatorConfig) extends ContinuousTask {
  override protected def onStart(): Unit = {
    elevator.setController(Signal.constant(config.collectSpeed).toPeriodic)
  }

  override protected def onEnd(): Unit = {
    elevator.resetToDefault()
  }
}
