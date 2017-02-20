package com.lynbrookrobotics.seventeen.shooter

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.seventeen.shooter.flywheel.velocityTasks._
import com.lynbrookrobotics.seventeen.agitator.{Agitator, SpinAgitator}
import com.lynbrookrobotics.seventeen.shooter.feeder.{RunFeeder, ShooterFeeder, ShooterFeederProperties}
import com.lynbrookrobotics.seventeen.shooter.flywheel.{ShooterFlywheel, ShooterFlywheelHardware, ShooterFlywheelProperties}
import com.lynbrookrobotics.seventeen.shooter.shifter.ShooterShifter

object ShooterTasks {
  def continuousShoot(implicit feeder: ShooterFeeder, shifter: ShooterShifter,
                      agitator: Agitator, flywheel: ShooterFlywheel,
                      flywheelProperties: Signal[ShooterFlywheelProperties],
                      feederProperties: Signal[ShooterFeederProperties],
                      flywheelHardware: ShooterFlywheelHardware): ContinuousTask = {
    val wrapper = new WhileAtVelocity(
      flywheelProperties.get.shootSpeed,
      flywheelProperties.get.speedTolerance
    )

    wrapper(
      new SpinAgitator() and new RunFeeder()
    )
  }
}
