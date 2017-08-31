package com.lynbrookrobotics.seventeen.shooter

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.seventeen.agitator.{Agitator, AgitatorProperties, SpinAgitator}
import com.lynbrookrobotics.seventeen.collector.elevator.{CollectorElevator, CollectorElevatorProperties, LoadIntoStorage}
import com.lynbrookrobotics.seventeen.collector.extender.{CollectorExtender, ExtendCollector}
import com.lynbrookrobotics.seventeen.collector.rollers.{CollectorRollers, CollectorRollersProperties, RollBallsInCollector}
import com.lynbrookrobotics.seventeen.loadtray.{ExtendTray, LoadTray}
import com.lynbrookrobotics.seventeen.shooter.flywheel.velocityTasks._
import com.lynbrookrobotics.seventeen.shooter.flywheel.{ShooterFlywheel, ShooterFlywheelHardware, ShooterFlywheelProperties}
import squants.time.Frequency

object ShooterTasks {
  def continuousShoot(shootSpeedLeft: Signal[Frequency],
                      shootSpeedRight: Signal[Frequency])
                     (implicit collectorElevator: CollectorElevator,
                      collectorRollers: CollectorRollers,
                      agitator: Agitator, flywheel: ShooterFlywheel,
                      collectorExtender: CollectorExtender,
                      loadTray: LoadTray,
                      agitatorProperties: Signal[AgitatorProperties],
                      flywheelProperties: Signal[ShooterFlywheelProperties],
                      collectorElevatorProperties: Signal[CollectorElevatorProperties],
                      collectorRollersProperties: Signal[CollectorRollersProperties],
                      flywheelHardware: ShooterFlywheelHardware): ContinuousTask = {
    val wrapper = new WhileAtDoubleVelocity(
      /*shootSpeedLeft*/???,
      /*shootSpeedRight*/???,
      flywheelProperties.get.speedTolerance
    )

    wrapper(
      new SpinAgitator()
        .and(new LoadIntoStorage())
        .and(???/*new RollBallsInCollector(collectorRollersProperties.map(_.highRollerSpeedOutput))*/)
        .and(new ExtendCollector())
    ).and(new ExtendTray())
  }
}
