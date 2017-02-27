package com.lynbrookrobotics.seventeen.drivetrain


import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.commons.drivetrain.TwoSidedDriveHardware
import com.lynbrookrobotics.potassium.frc.TalonEncoder
import com.lynbrookrobotics.potassium.units._
import com.lynbrookrobotics.seventeen.driver.DriverHardware
import com.ctre.CANTalon
import com.lynbrookrobotics.potassium.sensors.imu.{ADIS16448, DigitalGyro}
import edu.wpi.first.wpilibj.SPI
import squants.motion.{AngularVelocity, DegreesPerSecond}
import squants.space.Degrees
import squants.time.{Milliseconds, Seconds}
import squants.{Angle, Each, Length, Velocity}
import com.lynbrookrobotics.potassium.frc.Implicits._

case class DrivetrainHardware(leftBack: CANTalon, leftFront: CANTalon,
                                       rightBack: CANTalon, rightFront: CANTalon,
                                       gyro: DigitalGyro,
                                       props: DrivetrainProperties,
                                       driverHardware: DriverHardware)
  extends TwoSidedDriveHardware {
  val leftEncoder = new TalonEncoder(leftBack, Degrees(360) / Each(8192))
  val rightEncoder = new TalonEncoder(rightBack, -Degrees(360) / Each(8192))

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

  val pos = gyro.position.toPollingSignal(Milliseconds(5))

  override lazy val turnVelocity: Signal[AngularVelocity] = gyro.velocityZ.peek.
    map(_.getOrElse(DegreesPerSecond(0)))
  override lazy val turnPosition: Signal[Angle] = pos.map(_.map(_.z).getOrElse(Degrees(0)))
}

object DrivetrainHardware {
  def apply(config: DrivetrainConfig, driverHardware: DriverHardware): DrivetrainHardware = {
    DrivetrainHardware(
      new CANTalon(config.ports.leftBack),
      new CANTalon(config.ports.leftFront),
      new CANTalon(config.ports.rightBack),
      new CANTalon(config.ports.rightFront),
      new ADIS16448(new SPI(SPI.Port.kMXP), null),
      config.properties,
      driverHardware
    )
  }
}