package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.seventeen.driver.DriverConfig
import com.lynbrookrobotics.seventeen.drivetrain.{DrivetrainConfig, DrivetrainPorts, DrivetrainProperties}
import squants.motion.{DegreesPerSecond, FeetPerSecond, MetersPerSecondSquared}
import upickle.default._
import com.lynbrookrobotics.potassium.config.SquantsPickling._
import com.lynbrookrobotics.potassium.control.PIDConfig
import squants.space._
import com.lynbrookrobotics.potassium.units._
import GenericValue._
import squants.Percent
import squants.time.Seconds

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
        leftBack = 4,
        leftFront = 3,
        rightBack = 0,
        rightFront = 1
      ),
      properties = DrivetrainProperties(
        maxLeftVelocity = FeetPerSecond(22.9),
        maxRightVelocity = FeetPerSecond(27),
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
          Percent(10) / FeetPerSecond(1),
          Percent(0) / Meters(1),
          Percent(0) / MetersPerSecondSquared(1)
        ),
        rightControlGains = PIDConfig(
          Percent(10) / FeetPerSecond(1),
          Percent(0) / Meters(1),
          Percent(0) / MetersPerSecondSquared(1)
        )
      )
    ),
    agitator = null,
    climberPuller = null,
    collectorElevator = null,
    collectorExtender = null,
    collectorRollers = null,
    gearGrabber = null,
    gearTilter = null,
    shooterFeeder = null,
    shooterFlywheel = null,
    shooterShifter = null
  )

  println(write(config))
}
