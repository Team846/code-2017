package com.lynbrookrobotics.seventeen.drivetrain


import com.ctre.CANTalon
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.drivetrain.TwoSidedDriveHardware
import com.lynbrookrobotics.potassium.frc.Implicits._
import com.lynbrookrobotics.potassium.frc.{TalonEncoder, WPIClock}
import com.lynbrookrobotics.potassium.sensors.imu.{ADIS16448, DigitalGyro}
import com.lynbrookrobotics.potassium.units._
import com.lynbrookrobotics.seventeen.driver.DriverHardware
import edu.wpi.first.wpilibj.SPI
import squants.motion.{AngularVelocity, DegreesPerSecond}
import squants.space.Degrees
import squants.time.{Milliseconds, Seconds}
import squants.{Angle, Each, Length, Time, Velocity}
import edu.wpi.first.wpilibj.Timer
import java.io.{File, PrintWriter}

case class DrivetrainData(leftEncoderVelocity: AngularVelocity,
                          rightEncoderVelocity: AngularVelocity,
                          leftEncoderRotation: Angle,
                          rightEncoderRotation: Angle,
                          gyroVelocities: Value3D[AngularVelocity])

case class DrivetrainHardware(leftBack: CANTalon, leftFront: CANTalon,
                              rightBack: CANTalon, rightFront: CANTalon,
                              gyro: DigitalGyro,
                              props: DrivetrainProperties,
                              driverHardware: DriverHardware,
                              period: Time)(implicit clock: Clock)
  extends TwoSidedDriveHardware {
  val leftEncoder = new TalonEncoder(leftBack, Degrees(360) / Each(8192))
  val rightEncoder = new TalonEncoder(rightBack, -Degrees(360) / Each(8192))

  val wheelRadius = props.wheelDiameter / 2
  val track = props.track
  val tstats = new TimeStats(200, 500)
  val t = new Timer()
  val writer = new PrintWriter(new File(s"/home/lvuser/timelog"))

  val rootDataStream = Stream.periodic(period)(
    DrivetrainData(
      leftEncoder.getAngularVelocity,
      rightEncoder.getAngularVelocity,

      leftEncoder.getAngle,
      rightEncoder.getAngle,

      gyro.getVelocities
    )
  )

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

  tstats.Record(writer, t.get())
}

object DrivetrainHardware {
  def apply(config: DrivetrainConfig, driverHardware: DriverHardware)(implicit clock: Clock): DrivetrainHardware = {
    DrivetrainHardware(
      new CANTalon(config.ports.leftBack),
      new CANTalon(config.ports.leftFront),
      new CANTalon(config.ports.rightBack),
      new CANTalon(config.ports.rightFront),
      new ADIS16448(new SPI(SPI.Port.kMXP), null),
      config.properties,
      driverHardware,
      Milliseconds(5)
    )
  }
}