package com.lynbrookrobotics.seventeen.drivetrain


import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.commons.drivetrain.TwoSidedDriveHardware
import com.lynbrookrobotics.potassium.frc.TalonEncoder
import com.lynbrookrobotics.potassium.units._
import com.lynbrookrobotics.seventeen.driver.DriverHardware

import com.ctre.CANTalon

import squants.motion.{AngularVelocity, FeetPerSecond, RadiansPerSecond}
import squants.space.{Degrees, Inches, Radians}
import squants.time.Seconds
import squants.{Angle, Each, Length, Velocity}

case class DrivetrainHardware(leftBack: CANTalon, leftFront: CANTalon,
                              rightBack: CANTalon, rightFront: CANTalon,
                              driverHardware: DriverHardware)
  extends TwoSidedDriveHardware {
  val leftEncoder = new TalonEncoder(leftFront, -Degrees(360) / Each(8192))
  val rightEncoder = new TalonEncoder(rightBack, Degrees(360) / Each(8192))

  override val leftVelocity: Signal[Velocity] =
    leftEncoder.angularVelocity.map(av => FeetPerSecond(av.toRadiansPerSecond / 2.13 * 0.6))
  override val rightVelocity: Signal[Velocity] =
    rightEncoder.angularVelocity.map(av => FeetPerSecond(av.toRadiansPerSecond / 2.13 * 0.6))

  override def turnVelocity: Signal[AngularVelocity] = {
    rightVelocity.zip(leftVelocity).map { case (r, l) =>
      RadiansPerSecond((((l - r) * Seconds(1)) / Inches(21.75)) / 2)
    }
  }

  val WheelDiameter = Inches(6)
  val WheelRadius = WheelDiameter / 2

  val leftPosition = leftEncoder.angle.map(a => (a.toRadians / 2.13) * WheelRadius)
  val rightPosition = rightEncoder.angle.map(a => (a.toRadians / 2.13) * WheelRadius)

  override def forwardPosition: Signal[Length] =
    leftPosition.zip(rightPosition).map(t => (t._1 + t._2) / 2)

  override def turnPosition: Signal[Angle] =
    leftPosition.zip(rightPosition).map(t =>
      Radians(((t._1 - t._2) / Inches(21.75))))
}

object DrivetrainHardware {
  def apply(config: DrivetrainConfig, driverHardware: DriverHardware): DrivetrainHardware = {
    DrivetrainHardware(
      new CANTalon(config.ports.leftBack),
      new CANTalon(config.ports.leftFront),
      new CANTalon(config.ports.rightBack),
      new CANTalon(config.ports.rightFront),
      driverHardware
    )
  }
}