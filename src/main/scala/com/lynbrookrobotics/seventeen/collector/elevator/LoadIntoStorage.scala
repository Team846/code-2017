package com.lynbrookrobotics.seventeen.collector.elevator

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.potassium.streams.Stream
import squants.time.Seconds

class LoadIntoStorage(implicit elevator: CollectorElevator, props: Signal[CollectorElevatorProperties], clock: Clock) extends ContinuousTask {
  override protected def onStart(): Unit = {
    elevator.setController(Stream.periodic(Seconds(0.01))(props.get.collectSpeed))
  }

  override protected def onEnd(): Unit = {
    elevator.resetToDefault()
  }
}
