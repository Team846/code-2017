package com.lynbrookrobotics.seventeen.collector

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.seventeen.collector.elevator.{CollectorElevator, CollectorElevatorProperties, LoadIntoStorage}
import com.lynbrookrobotics.seventeen.collector.extender.{CollectorExtender, ExtendCollector}
import com.lynbrookrobotics.seventeen.collector.rollers.{CollectorRollers, CollectorRollersProperties, RollBallsInCollector}
import com.lynbrookrobotics.seventeen.loadtray.{ExtendTray, LoadTray}
import squants.Dimensionless

object CollectorTasks {
  def collect(collectingSpeed: Signal[Dimensionless])(implicit extender: CollectorExtender,
                                                      elevator: CollectorElevator,
                                                      rollers: CollectorRollers,
                                                      loadTray: LoadTray,
                                                      elevatorProps: Signal[CollectorElevatorProperties],
                                                      rollerProps: Signal[CollectorRollersProperties]): ContinuousTask = {
    new ExtendCollector()
      .and(new RollBallsInCollector(collectingSpeed))
      .and(new LoadIntoStorage())
      .and(new ExtendTray())
  }
}
