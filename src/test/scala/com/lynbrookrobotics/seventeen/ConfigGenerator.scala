package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.seventeen.driver.DriverConfig
import com.lynbrookrobotics.seventeen.drivetrain.{DrivetrainConfig, DrivetrainPorts, DrivetrainProperties}
import squants.motion.{DegreesPerSecond, FeetPerSecond, MetersPerSecondSquared}
import upickle.default._
import com.lynbrookrobotics.potassium.config.SquantsPickling._
import com.lynbrookrobotics.potassium.control.{PIDConfig, PIDFConfig}
import squants.space._
import com.lynbrookrobotics.potassium.units._
import GenericValue._
import com.lynbrookrobotics.seventeen.collector.elevator.{CollectorElevatorConfig, CollectorElevatorPorts, CollectorElevatorProperties}
import com.lynbrookrobotics.seventeen.collector.extender.{CollectorExtenderConfig, CollectorExtenderPorts}
import com.lynbrookrobotics.seventeen.collector.rollers.{CollectorRollersConfig, CollectorRollersPorts, CollectorRollersProperties}
import com.lynbrookrobotics.seventeen.gear.grabber.{GearGrabberConfig, GearGrabberPorts, GearGrabberProperties}
import com.lynbrookrobotics.seventeen.gear.tilter.{GearTilterConfig, GearTilterPorts}
import com.lynbrookrobotics.seventeen.shooter.flywheel.{ShooterFlywheelConfig, ShooterFlywheelPorts, ShooterFlywheelProperties}
import squants.{Each, Percent}
import squants.time.{Minutes, RevolutionsPerMinute, Seconds}

object ConfigGenerator extends App {
  val config = RobotConfig(
    driver = DriverConfig(
      driverPort = 0,
      operatorPort = 1,
      driverWheelPort = 2,
      launchpadPort = -1
    ),
    drivetrain = DrivetrainConfig(
      ports = DrivetrainPorts(
        leftBack = 13,
        leftFront = 12,
        rightBack = 11,
        rightFront = 10
      ),
      properties = DrivetrainProperties(
        maxLeftVelocity = FeetPerSecond(28.9),
        maxRightVelocity = FeetPerSecond(29.1),
        maxAcceleration = MetersPerSecondSquared(0),
        wheelDiameter = Inches(6),
        track = Inches(21.75),
        gearRatio = 1 / 2.13,
        turnControlGains = PIDConfig(
          Percent(0) / DegreesPerSecond(1),
          Percent(0) / Degrees(1),
          Percent(0) / (DegreesPerSecond(1).toGeneric / Seconds(1))
        ),
        forwardPositionControlGains = PIDConfig(
          Percent(100) / Feet(3.5),
          Percent(0) / (Feet(1).toGeneric * Seconds(1)),
          Percent(0) / FeetPerSecond(1)
        ),
        turnPositionControlGains = PIDConfig(
          Percent(75) / Degrees(90),
          Percent(0) / (Degrees(1).toGeneric * Seconds(1)),
          Percent(0) / DegreesPerSecond(1)
        ),
        leftControlGains = PIDConfig(
          Percent(10) / FeetPerSecond(5),
          Percent(0) / Meters(1),
          Percent(0) / MetersPerSecondSquared(1)
        ),
        rightControlGains = PIDConfig(
          Percent(10) / FeetPerSecond(5),
          Percent(0) / Meters(1),
          Percent(0) / MetersPerSecondSquared(1)
        )
      )
    ),
    agitator = null,
    climberPuller = null,
    collectorElevator = CollectorElevatorConfig(
      port = CollectorElevatorPorts(
        motor = 1
      ),
      properties = CollectorElevatorProperties(
        collectSpeed = Percent(100)
      )
    ),
    collectorExtender = CollectorExtenderConfig(
      port = CollectorExtenderPorts(
        pneumatic = 0
      )
    ),
    collectorRollers = CollectorRollersConfig(
      ports = CollectorRollersPorts(
        rollerChannel = 0
      ),
      properties = CollectorRollersProperties(
        lowRollerSpeedOutput = Percent(0),
        highRollerSpeedOutput = Percent(100)
      )
    ),
    gearGrabber = GearGrabberConfig(
      port = GearGrabberPorts(
        pneumatic = 1,
        proximitySensor = 0
      ),
      props = GearGrabberProperties(
        detectingDistance = Inches(0)
      )
    ),
    gearTilter = GearTilterConfig(
      port = GearTilterPorts(
        pneumatic = 2
      )
    ),
    shooterFeeder = null,
    shooterFlywheel = null,
    //    shooterFlywheel = ShooterFlywheelConfig(
    //      ports = ShooterFlywheelPorts(
    //        leftMotor = 3,
    //        rightMotor = 2,
    //        leftHall = 0,
    //        rightHall = 1
    //      ),
    //      props = ShooterFlywheelProperties(
    //        maxVelocityLeft = RevolutionsPerMinute(6500),
    //        maxVelocityRight = RevolutionsPerMinute(6500),
    //        velocityGainsLeft = PIDConfig(
    //          kp = Ratio(Percent(60), RevolutionsPerMinute(1000)),
    //          ki = Ratio(Percent(0), Each(1000)),
    //          kd = Percent(0) / (RevolutionsPerMinute(1000).toGeneric / Seconds(1))
    //        ),
    //        velocityGainsRight = PIDConfig(
    //          kp = Ratio(Percent(60), RevolutionsPerMinute(1000)),
    //          ki = Ratio(Percent(0), Each(1000)),
    //          kd = Percent(0) / (RevolutionsPerMinute(1000).toGeneric / Seconds(1))
    //        ),
    //        lowShootSpeedLeft = RevolutionsPerMinute(1000),
    //        lowShootSpeedRight = RevolutionsPerMinute(1000),
    //        midShootSpeedLeft = RevolutionsPerMinute(1000),
    //        midShootSpeedRight = RevolutionsPerMinute(1000),
    //        fastShootSpeedLeft = RevolutionsPerMinute(1000),
    //        fastShootSpeedRight= RevolutionsPerMinute(1000),
    //        speedTolerance = RevolutionsPerMinute(0)
    //      )
    //    ),
    shooterShifter = null
  )

  println(write(config))
}
