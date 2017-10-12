package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.seventeen.agitator.AgitatorConfig
import com.lynbrookrobotics.seventeen.camselect.CamSelectConfig
import com.lynbrookrobotics.seventeen.climber.puller.ClimberPullerConfig
import com.lynbrookrobotics.seventeen.collector.elevator.CollectorElevatorConfig
import com.lynbrookrobotics.seventeen.collector.extender.CollectorExtenderConfig
import com.lynbrookrobotics.seventeen.collector.rollers.CollectorRollersConfig
import com.lynbrookrobotics.seventeen.driver.DriverConfig
import com.lynbrookrobotics.seventeen.drivetrain.DrivetrainConfig
import com.lynbrookrobotics.seventeen.gear.roller.GearRollerConfig
import com.lynbrookrobotics.seventeen.gear.tilter.GearTilterConfig
import com.lynbrookrobotics.seventeen.loadtray.LoadTrayConfig
import com.lynbrookrobotics.seventeen.shooter.flywheel.ShooterFlywheelConfig
import com.lynbrookrobotics.seventeen.shooter.shifter.ShooterShifterConfig

case class RobotConfig(driver: DriverConfig,
                       drivetrain: DrivetrainConfig,
                       agitator: AgitatorConfig,
                       camSelect: CamSelectConfig,
                       climberPuller: ClimberPullerConfig,
                       collectorElevator: CollectorElevatorConfig,
                       collectorExtender: CollectorExtenderConfig,
                       collectorRollers: CollectorRollersConfig,
                       gearRoller: GearRollerConfig,
                       gearTilter: GearTilterConfig,
                       shooterFlywheel: ShooterFlywheelConfig,
                       shooterShifter: ShooterShifterConfig,
                       loadTray: LoadTrayConfig)
