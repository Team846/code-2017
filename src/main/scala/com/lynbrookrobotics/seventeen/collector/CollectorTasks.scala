package com.lynbrookrobotics.seventeen.collector

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.seventeen.collector.elevator.{CollectorElevator, CollectorElevatorProperties, LoadIntoStorage}
import com.lynbrookrobotics.seventeen.collector.extender.{CollectorExtender, ExtendCollector}
import com.lynbrookrobotics.seventeen.collector.rollers.{CollectorRollers, CollectorRollersProperties, RollBallsInCollector}

object CollectorTasks {
  def collect(implicit extender: CollectorExtender,
              elevator: CollectorElevator,
              rollers: CollectorRollers,
              elevatorProps: Signal[CollectorElevatorProperties],
              rollerProps: Signal[CollectorRollersProperties]): ContinuousTask = {
    new ExtendCollector()
      .and(new RollBallsInCollector())
      .and(new LoadIntoStorage())
  }
}