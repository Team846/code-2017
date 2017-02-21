package com.lynbrookrobotics.seventeen.shooter

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.seventeen.ShooterFlywheelState
import com.lynbrookrobotics.seventeen.shooter.flywheel.velocityTasks._
import com.lynbrookrobotics.seventeen.agitator.{Agitator, SpinAgitator}
import com.lynbrookrobotics.seventeen.shooter.feeder.{RunFeeder, ShooterFeeder, ShooterFeederProperties}
import com.lynbrookrobotics.seventeen.shooter.flywheel.{ShooterFlywheel, ShooterFlywheelHardware, ShooterFlywheelProperties}
import com.lynbrookrobotics.seventeen.shooter.shifter.ShooterShifter

object ShooterTasks {
  def continuousShoot(shootSpeed: ShooterFlywheelState)(implicit feeder: ShooterFeeder, shifter: ShooterShifter,
                                                   agitator: Agitator, flywheel: ShooterFlywheel,
                                                   flywheelProperties: Signal[ShooterFlywheelProperties],
                                                   feederProperties: Signal[ShooterFeederProperties],
                                                   flywheelHardware: ShooterFlywheelHardware): ContinuousTask = {
    val wrapper = new WhileAtVelocity(
      shootSpeed.speed,
      flywheelProperties.get.speedTolerance
    )

    wrapper(
      new SpinAgitator() and new RunFeeder()
    )
  }
}
