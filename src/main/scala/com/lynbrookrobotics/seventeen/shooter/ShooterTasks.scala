package com.lynbrookrobotics.seventeen.shooter

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.{ContinuousTask, WaitTask}
import com.lynbrookrobotics.seventeen.agitator.{Agitator, AgitatorProperties, SpinAgitator}
import com.lynbrookrobotics.seventeen.collector.elevator.{CollectorElevator, CollectorElevatorProperties, LoadIntoStorage}
import com.lynbrookrobotics.seventeen.collector.extender.{CollectorExtender, ExtendCollector}
import com.lynbrookrobotics.seventeen.collector.rollers.{CollectorRollers, CollectorRollersProperties, RollBallsInCollector}
import com.lynbrookrobotics.seventeen.loadtray.{ExtendTray, LoadTray}
import com.lynbrookrobotics.seventeen.shooter.flywheel.velocityTasks._
import com.lynbrookrobotics.seventeen.shooter.flywheel.{ShooterFlywheel, ShooterFlywheelHardware, ShooterFlywheelProperties}
import squants.time.{Frequency, Seconds}

object ShooterTasks {
  def continuousShoot(shootSpeedLeft: Stream[Frequency],
                      shootSpeedRight: Stream[Frequency])
                     (collectorElevator: CollectorElevator,
                      collectorRollers: CollectorRollers,
                      agitator: Agitator, flywheel: ShooterFlywheel,
                      collectorExtender: CollectorExtender,
                      loadTray: LoadTray)
                     (implicit agitatorProperties: Signal[AgitatorProperties],
                      flywheelProperties: Signal[ShooterFlywheelProperties],
                      collectorElevatorProperties: Signal[CollectorElevatorProperties],
                      collectorRollersProperties: Signal[CollectorRollersProperties],
                      flywheelHardware: ShooterFlywheelHardware,
                      clock: Clock): ContinuousTask = {
    val wrapper = new WhileAtDoubleVelocity(
      shootSpeedLeft,
      shootSpeedRight,
      flywheelProperties.get.speedTolerance
    )(flywheel)

    val runCollector = new LoadIntoStorage(collectorElevator)
      .and(new RollBallsInCollector(shootSpeedLeft.map(_ => collectorRollersProperties.get.highRollerSpeedOutput))(collectorRollers))
      .and(new ExtendCollector(collectorExtender))

    // wait 0.5 seconds for the collector to spin up, but start spinning up the flywheel too
    val spinUp = new WaitTask(Seconds(0.5))

    spinUp.then(
      wrapper(
        // once we are at speed, we continue running the collector but also start the agitator
        new SpinAgitator(agitator)
      )
    ).and(new ExtendTray(loadTray).and(runCollector)) // extend the tray while we are trying to shoot
  }
}
