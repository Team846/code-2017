package com.lynbrookrobotics.seventeen.drivetrain


import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSidedDriveHardware
import com.lynbrookrobotics.potassium.frc.Implicits._
import com.lynbrookrobotics.potassium.frc.{LazyTalon, TalonEncoder}
import com.lynbrookrobotics.potassium.sensors.imu.{ADIS16448, DigitalGyro}
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.units._
import com.lynbrookrobotics.seventeen.driver.DriverHardware
import edu.wpi.first.wpilibj.SPI
import squants.motion.AngularVelocity
import squants.space.Degrees
import squants.time.{Milliseconds, Seconds}
import squants.{Angle, Each, Length, Time, Velocity}

case class DrivetrainData(leftEncoderVelocity: AngularVelocity,
                          rightEncoderVelocity: AngularVelocity,
                          leftEncoderRotation: Angle,
                          rightEncoderRotation: Angle,
                          gyroVelocities: Value3D[AngularVelocity])

case class DrivetrainHardware(leftBack: LazyTalon, leftFront: LazyTalon,
                              rightBack: LazyTalon, rightFront: LazyTalon,
                              gyro: DigitalGyro,
                              props: DrivetrainProperties,
                              driverHardware: DriverHardware,
                              period: Time)(implicit clock: Clock)
  extends TwoSidedDriveHardware {
  val leftEncoder = new TalonEncoder(leftBack.t, Degrees(360) / Each(8192))
  val rightEncoder = new TalonEncoder(rightBack.t, -Degrees(360) / Each(8192))

  val wheelRadius: Length = props.wheelDiameter / 2
  val track: Length = props.track

  val rootDataStream: Stream[DrivetrainData] = Stream.periodic(period) {
    DrivetrainData(
      leftEncoder.getAngularVelocity,
      rightEncoder.getAngularVelocity,

      leftEncoder.getAngle,
      rightEncoder.getAngle,

      gyro.getVelocities
    )
  }

  override val leftVelocity: Stream[Velocity] = rootDataStream.map(_.leftEncoderVelocity).map(av =>
    wheelRadius * (av.toRadiansPerSecond * props.gearRatio) / Seconds(1))
  override val rightVelocity: Stream[Velocity] = rootDataStream.map(_.rightEncoderVelocity).map(av =>
    wheelRadius * (av.toRadiansPerSecond * props.gearRatio) / Seconds(1))

  val leftPosition: Stream[Length] = rootDataStream.map(_.leftEncoderRotation).map(a =>
    a.toRadians * props.gearRatio * wheelRadius)
  val rightPosition: Stream[Length] = rootDataStream.map(_.rightEncoderRotation).map(a =>
    a.toRadians * props.gearRatio * wheelRadius)

  override lazy val turnVelocity: Stream[AngularVelocity] = rootDataStream.map(_.gyroVelocities).map(_.z)
  override lazy val turnPosition: Stream[Angle] = turnVelocity.integral
}

object DrivetrainHardware {
  def apply(config: DrivetrainConfig, driverHardware: DriverHardware)(implicit clock: Clock): DrivetrainHardware = {
    DrivetrainHardware(
      new TalonSRX(config.ports.leftBack),
      new TalonSRX(config.ports.leftFront),
      new TalonSRX(config.ports.rightBack),
      new TalonSRX(config.ports.rightFront),
      new ADIS16448(new SPI(SPI.Port.kMXP), null),
      config.properties,
      driverHardware,
      Milliseconds(5)
    )
  }
}