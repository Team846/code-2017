package com.lynbrookrobotics.seventeen.shooter

import com.lynbrookrobotics.seventeen.shooter.feeder.ShooterFeederConfig
import com.lynbrookrobotics.seventeen.shooter.flywheel.ShooterFlywheelConfig
import com.lynbrookrobotics.seventeen.shooter.shifter.ShoofterShifterConfig
import squants.Dimensionless
import squants.motion.AngularVelocity

case class ShooterProperties(flywheelSpeed: AngularVelocity,
                             flywheelSpeedTolerance: AngularVelocity)

case class ShooterConfig(shifterConfig: ShoofterShifterConfig, flywheelConfig: ShooterFlywheelConfig,
                         feederConfig: ShooterFeederConfig, properties: ShooterProperties)
