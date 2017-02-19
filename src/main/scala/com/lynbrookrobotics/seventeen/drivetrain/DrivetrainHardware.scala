package com.lynbrookrobotics.seventeen.drivetrain


import com.lynbrookrobotics.potassium.{PeriodicSignal, Signal}
import com.lynbrookrobotics.potassium.commons.drivetrain.TwoSidedDriveHardware
import com.lynbrookrobotics.potassium.frc.{SPIWrapper, TalonEncoder}
import com.lynbrookrobotics.potassium.units._
import com.lynbrookrobotics.seventeen.driver.DriverHardware
import com.ctre.CANTalon
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.units.Point
import com.lynbrookrobotics.potassium.sensors.imu.ADIS16448
import com.lynbrookrobotics.potassium.sensors.position.xyPosition
import edu.wpi.first.wpilibj.SPI
import squants.space.{Degrees, Feet}
import squants.time.{Milliseconds, Seconds}
import squants.{Angle, Each, Length, Velocity}

case class DrivetrainHardware(leftBack: CANTalon, leftFront: CANTalon,
                              rightBack: CANTalon, rightFront: CANTalon,
                              props: DrivetrainProperties,
                              driverHardware: DriverHardware)
                             (implicit clock: Clock)
  extends TwoSidedDriveHardware {
  val leftEncoder = new TalonEncoder(leftFront, -Degrees(360) / Each(8192))
  val rightEncoder = new TalonEncoder(rightBack, Degrees(360) / Each(8192))

  val wheelRadius = props.wheelDiameter / 2
  val track = props.track

  override val leftVelocity: Signal[Velocity] = leftEncoder.angularVelocity.map(av =>
    wheelRadius * (av.toRadiansPerSecond * props.gearRatio) / Seconds(1))
  override val rightVelocity: Signal[Velocity] = rightEncoder.angularVelocity.map(av =>
    wheelRadius * (av.toRadiansPerSecond * props.gearRatio) / Seconds(1))

  val leftPosition: Signal[Length] = leftEncoder.angle.map(a =>
    a.toRadians * props.gearRatio * wheelRadius)
  val rightPosition: Signal[Length] = rightEncoder.angle.map(a =>
    a.toRadians * props.gearRatio * wheelRadius)

  val zeroPosition = Value3D[Angle](Degrees(0), Degrees(0), Degrees(0))
  val imu = new ADIS16448(new SPIWrapper(new SPI(SPI.Port.kMXP)), Milliseconds(5))
  val anglePose = imu.position.toPollingSignal(Milliseconds(5)).map(
    _.getOrElse(zeroPosition))
  override val periodicPosition: PeriodicSignal[Point] = xyPosition(
    new Point(Feet(0), Feet(0)),
    anglePose.map(_.z),
    forwardPosition.toPeriodic
  )
  override val position: Signal[Point] = periodicPosition.toPollingSignal(Milliseconds(5)).map(
    _.getOrElse(new Point(Feet(0), Feet(0))))
}

object DrivetrainHardware {
  def apply(config: DrivetrainConfig, driverHardware: DriverHardware)(implicit clock: Clock): DrivetrainHardware = {
    DrivetrainHardware(
      new CANTalon(config.ports.leftBack),
      new CANTalon(config.ports.leftFront),
      new CANTalon(config.ports.rightBack),
      new CANTalon(config.ports.rightFront),
      config.properties,
      driverHardware
    )
  }
}