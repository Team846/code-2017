package com.lynbrookrobotics.seventeen.collector.elevator

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class LoadIntoStorage(implicit elevator: CollectorElevator, props: Signal[CollectorElevatorProperties]) extends ContinuousTask {
  override protected def onStart(): Unit = {
    elevator.setController(props.map(_.collectSpeed).toPeriodic)
  }

  override protected def onEnd(): Unit = {
    elevator.resetToDefault()
  }
}
