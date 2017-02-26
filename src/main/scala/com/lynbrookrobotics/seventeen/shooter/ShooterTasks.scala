package com.lynbrookrobotics.seventeen.shooter

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.seventeen.shooter.flywheel.velocityTasks._
import com.lynbrookrobotics.seventeen.agitator.{Agitator, SpinAgitator}
import com.lynbrookrobotics.seventeen.shooter.feeder.{RunFeeder, RunFeederSlow, ShooterFeeder, ShooterFeederProperties}
import com.lynbrookrobotics.seventeen.shooter.flywheel.{ShooterFlywheel, ShooterFlywheelHardware, ShooterFlywheelProperties}
import com.lynbrookrobotics.seventeen.shooter.shifter.{ShiftShooter, ShooterShifter, ShooterShifterState}
import squants.motion.AngularVelocity
import squants.time.Frequency

object ShooterTasks {
  def continuousShoot(shootSpeed: Signal[Frequency])(implicit feeder: ShooterFeeder, shifter: ShooterShifter,
                                                  agitator: Agitator, flywheel: ShooterFlywheel,
                                                  flywheelProperties: Signal[ShooterFlywheelProperties],
                                                  feederProperties: Signal[ShooterFeederProperties],
                                                  flywheelHardware: ShooterFlywheelHardware): ContinuousTask = {
    val wrapper = new WhileAtVelocity(
      shootSpeed,
      flywheelProperties.get.speedTolerance
    )

    wrapper(
      new SpinAgitator() and new RunFeeder()
    )
  }

  def continuousShootSlowly(shootSpeed: Signal[Frequency])(implicit feeder: ShooterFeeder, shifter: ShooterShifter,
                                                        agitator: Agitator, flywheel: ShooterFlywheel,
                                                        flywheelProperties: Signal[ShooterFlywheelProperties],
                                                        feederProperties: Signal[ShooterFeederProperties],
                                                        flywheelHardware: ShooterFlywheelHardware): ContinuousTask = {
    val wrapper = new WhileAtVelocity(
      shootSpeed,
      flywheelProperties.get.speedTolerance
    )

    wrapper(
      new SpinAgitator() and new RunFeederSlow()
    )
  }
}
