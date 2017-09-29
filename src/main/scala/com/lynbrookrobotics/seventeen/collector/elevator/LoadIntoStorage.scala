package com.lynbrookrobotics.seventeen.collector.elevator

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class LoadIntoStorage(elevator: CollectorElevator)(implicit props: Signal[CollectorElevatorProperties]) extends ContinuousTask {
  override protected def onStart(): Unit = {
    elevator.setController(elevator.coreTicks.map(_ => props.get.collectSpeed))
  }

  override protected def onEnd(): Unit = {
    elevator.resetToDefault()
  }
}
