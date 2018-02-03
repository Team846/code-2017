package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.control.PIDConfig
import com.lynbrookrobotics.potassium.units.GenericValue._
import com.lynbrookrobotics.potassium.units._
import com.lynbrookrobotics.seventeen.agitator.{AgitatorConfig, AgitatorPorts, AgitatorProperties}
import com.lynbrookrobotics.seventeen.camselect._
import com.lynbrookrobotics.seventeen.climber.puller.{ClimberPullerConfig, ClimberPullerPorts, ClimberPullerProperties}
import com.lynbrookrobotics.seventeen.collector.elevator.{CollectorElevatorConfig, CollectorElevatorPorts, CollectorElevatorProperties}
import com.lynbrookrobotics.seventeen.collector.extender.{CollectorExtenderConfig, CollectorExtenderPorts}
import com.lynbrookrobotics.seventeen.collector.rollers.{CollectorRollersConfig, CollectorRollersPorts, CollectorRollersProperties}
import com.lynbrookrobotics.seventeen.driver.DriverConfig
import com.lynbrookrobotics.seventeen.drivetrain.{DrivetrainConfig, DrivetrainPorts, DrivetrainProperties}
import com.lynbrookrobotics.seventeen.gear.grabber.{GearGrabberConfig, GearGrabberPorts, GearGrabberProperties}
import com.lynbrookrobotics.seventeen.gear.tilter.{GearTilterConfig, GearTilterPorts}
import com.lynbrookrobotics.seventeen.loadtray.{LoadTrayConfig, LoadTrayPorts}
import com.lynbrookrobotics.seventeen.shooter.flywheel.{ShooterFlywheelConfig, ShooterFlywheelPorts, ShooterFlywheelProperties}
import com.lynbrookrobotics.seventeen.shooter.shifter.{ShooterShifterConfig, ShooterShifterPorts}
import com.lynbrookrobotics.potassium.config.SquantsPickling._
import squants.electro.Volts
import squants.motion.{DegreesPerSecond, FeetPerSecond, MetersPerSecondSquared}
import squants.space._
import squants.time.{RevolutionsPerMinute, Seconds}
import squants.{Each, Percent}
import upickle.default._

object ConfigGenerator extends App {
  val config = RobotConfig(
    driver = DriverConfig(
      driverPort = 0,
      operatorPort = 0,
      driverWheelPort = 0,
      launchpadPort = 0
    ),
    drivetrain = DrivetrainConfig(
      ports = DrivetrainPorts(
        leftBack = 0,
        leftFront = 0,
        rightBack = 0,
        rightFront = 0
      ),
      properties = DrivetrainProperties(
        maxLeftVelocity = FeetPerSecond(0),
        maxRightVelocity = FeetPerSecond(0),
        maxAcceleration = MetersPerSecondSquared(0),
        wheelDiameter = Inches(0),
        track = Inches(0),
        gearRatio = 0,
        turnVelocityGains = PIDConfig(
          Percent(0) / DegreesPerSecond(1),
          Percent(0) / Degrees(1),
          Percent(0) / (DegreesPerSecond(1).toGeneric / Seconds(1))
        ),
        forwardPositionGains = PIDConfig(
          Percent(0) / Feet(3.5),
          Percent(0) / (Feet(1).toGeneric * Seconds(1)),
          Percent(0) / FeetPerSecond(1)
        ),
        turnPositionGains = PIDConfig(
          Percent(0) / Degrees(90),
          Percent(0) / (Degrees(1).toGeneric * Seconds(1)),
          Percent(0) / DegreesPerSecond(1)
        ),
        leftVelocityGains = PIDConfig(
          Percent(0) / FeetPerSecond(5),
          Percent(0) / Meters(1),
          Percent(0) / MetersPerSecondSquared(1)
        ),
        rightVelocityGains = PIDConfig(
          Percent(0) / FeetPerSecond(5),
          Percent(0) / Meters(1),
          Percent(0) / MetersPerSecondSquared(1)
        ),
        currentLimit = Percent(0),
        defaultLookAheadDistance = Feet(0.5),
        blendExponent = 1,
        robotLength = Feet(0)
      ),
      idx = 0
    ),
    agitator = AgitatorConfig(
      ports = AgitatorPorts(
        motor = 0
      ),
      properties = AgitatorProperties(
        spinSpeed = Percent(0)
      )
    ),
    climberPuller = ClimberPullerConfig(
      ports = ClimberPullerPorts(
        motorChannelA = 0,
        motorChannelB = 0
      ),
      props = ClimberPullerProperties(
        climbSpeed = Percent(0)
      )
    ),
    camSelect = CamSelectConfig(
      port = CamSelectPorts(
        leftCamPort = 5804,
        rightCamPort = 5805,
        driveCamPort = 5803
      ),
      properties = CamSelectProperties(
        coprocessorHostname = "10.8.46.19",
        mjpegPath = "/stream.mjpg"
      )
    ),
    collectorElevator = CollectorElevatorConfig(
      port = CollectorElevatorPorts(
        motor = 0
      ),
      properties = CollectorElevatorProperties(
        collectSpeed = Percent(0)
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
        highRollerSpeedOutput = Percent(0)
      )
    ),
    gearGrabber = GearGrabberConfig(
      port = GearGrabberPorts(
        pneumatic = 0,
        proximitySensor = 0
      ),
      props = GearGrabberProperties(
        detectingDistance = Volts(0)
      )
    ),
    gearTilter = GearTilterConfig(
      port = GearTilterPorts(
        pneumatic = 0
      )
    ),
    shooterFlywheel = ShooterFlywheelConfig(
      ports = ShooterFlywheelPorts(
        leftMotor = 0,
        rightMotor = 0,
        leftHall = 0,
        rightHall = 0
      ),
      props = ShooterFlywheelProperties(
        maxVelocityLeft = RevolutionsPerMinute(0),
        maxVelocityRight = RevolutionsPerMinute(0),
        velocityGainsLeft = PIDConfig(
          kp = Ratio(Percent(0), RevolutionsPerMinute(1000)),
          ki = Ratio(Percent(0), Each(1000)),
          kd = Percent(0) / (RevolutionsPerMinute(1000).toGeneric / Seconds(1))
        ),
        velocityGainsRight = PIDConfig(
          kp = Ratio(Percent(0), RevolutionsPerMinute(1000)),
          ki = Ratio(Percent(0), Each(1000)),
          kd = Percent(0) / (RevolutionsPerMinute(1000).toGeneric / Seconds(1))
        ),
        lowShootSpeedLeft = RevolutionsPerMinute(0),
        lowShootSpeedRight = RevolutionsPerMinute(0),
        midShootSpeedLeft = RevolutionsPerMinute(0),
        midShootSpeedRight = RevolutionsPerMinute(0),
        fastShootSpeedLeft = RevolutionsPerMinute(0),
        fastShootSpeedRight = RevolutionsPerMinute(0),
        currentLimit = Percent(0),
        speedTolerance = RevolutionsPerMinute(0)
      )
    ),
    shooterShifter = ShooterShifterConfig(
      ports = ShooterShifterPorts(
        pneumatic = 0
      )
    ),
    loadTray = LoadTrayConfig(
      port = LoadTrayPorts(
        pneumatic = 0
      )
    )
  )

  println(write(config))
}
