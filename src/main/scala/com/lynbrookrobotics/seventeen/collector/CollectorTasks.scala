package com.lynbrookrobotics.seventeen.collector

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.seventeen.collector.elevator.{CollectorElevator, CollectorElevatorProperties, LoadIntoStorage}
import com.lynbrookrobotics.seventeen.collector.extender.{CollectorExtender, ExtendCollector}
import com.lynbrookrobotics.seventeen.collector.rollers.{CollectorRollers, CollectorRollersProperties, RollBallsInCollector}
import com.lynbrookrobotics.seventeen.loadtray.{ExtendTray, LoadTray}
import squants.Dimensionless

object CollectorTasks {
  def collect(collectingSpeed: Stream[Dimensionless])
             (extender: CollectorExtender,
              elevator: CollectorElevator,
              rollers: CollectorRollers,
              loadTray: LoadTray)
             (implicit elevatorProps: Signal[CollectorElevatorProperties],
              rollerProps: Signal[CollectorRollersProperties]): ContinuousTask = {
    new ExtendCollector(extender)
      .and(new RollBallsInCollector(collectingSpeed)(rollers))
      .and(new LoadIntoStorage(elevator))
      .and(new ExtendTray(loadTray))
  }
}
