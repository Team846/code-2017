package com.lynbrookrobotics.seventeen.shooter

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.FiniteTask
import com.lynbrookrobotics.seventeen.shooter.flywheel.velocityTasks._
import com.lynbrookrobotics.seventeen.agitator.{Agitator, SpinAgitator}
import com.lynbrookrobotics.seventeen.shooter.feeder.{RunFeeder, ShooterFeeder, ShooterFeederConfig, ShooterFeederProperties}
import com.lynbrookrobotics.seventeen.shooter.flywheel.{ShooterFlywheel, ShooterFlywheelHardware, ShooterFlywheelProperties}
import com.lynbrookrobotics.seventeen.shooter.shifter.ShooterShifter

object ShooterTasks {
  def continuousShoot(implicit feeder: ShooterFeeder, shifter: ShooterShifter,
                      agitator: Agitator, flywheel: ShooterFlywheel,
                      config: ShooterConfig, flywheelProperties: Signal[ShooterFlywheelProperties],
                      shooterProperties: Signal[ShooterProperties], feederProperties: Signal[ShooterFeederProperties],
                      flywheelHardware: ShooterFlywheelHardware): FiniteTask = {
    val whileAtVelocity = new SpinAgitator().
      and(new RunFeeder())

    val withoutSpinning = new WaitForVelocity(shooterProperties.get.flywheelSpeed, shooterProperties.get.flywheelSpeedTolerance)
    (whileAtVelocity.and(new SpinAtVelocity(shooterProperties.get.flywheelSpeed)))

    withoutSpinning
  }
}
