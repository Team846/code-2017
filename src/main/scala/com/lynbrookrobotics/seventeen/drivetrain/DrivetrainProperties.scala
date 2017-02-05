package com.lynbrookrobotics.seventeen.drivetrain

import com.lynbrookrobotics.potassium.commons.drivetrain.{TwoSidedDriveProperties, VelocityGains}
import com.lynbrookrobotics.potassium.control.PIDFConfig
import com.lynbrookrobotics.potassium.units.GenericValue._
import com.lynbrookrobotics.potassium.units.{GenericDerivative, GenericIntegral, GenericValue, _}
import squants.motion._
import squants.space.{Degrees, Feet, Inches, Meters}
import squants.time.Seconds
import squants.{Dimensionless, Percent, Velocity}

case class DrivetrainProperties(maxLeftVelocity: Velocity, maxRightVelocity: Velocity) extends TwoSidedDriveProperties {
  override val maxTurnVelocity: AngularVelocity =
    RadiansPerSecond((((maxLeftVelocity + maxRightVelocity) * Seconds(1)) / Inches(21.75)) / 2)

  override def turnControlGains = PIDFConfig(
    Percent(0) / DegreesPerSecond(1),
    Percent(0) / Degrees(1),
    Percent(0) / (DegreesPerSecond(1).toGeneric / Seconds(1)),
    Percent(100) / maxTurnVelocity
  )

  override val forwardPositionControlGains = PIDFConfig(
    Percent(100) / Feet(3.5),
    Percent(0) / (Feet(1).toGeneric * Seconds(1)),
    Percent(0) / FeetPerSecond(1),
    Percent(0) / Meters(1)
  )

  override val turnPositionControlGains = PIDFConfig(
    Percent(75) / Degrees(90),
    Percent(0) / (Degrees(1).toGeneric * Seconds(1)),
    Percent(0) / DegreesPerSecond(1),
    Percent(0) / Degrees(1)
  )

  override lazy val leftControlGains: VelocityGains = PIDFConfig(
    Percent(10) / FeetPerSecond(1),
    Percent(0) / Meters(1),
    Percent(0) / MetersPerSecondSquared(1),
    Percent(100) / maxLeftVelocity
  )

  override lazy val rightControlGains: VelocityGains = PIDFConfig(
    Percent(10) / FeetPerSecond(1),
    Percent(0) / Meters(1),
    Percent(0) / MetersPerSecondSquared(1),
    Percent(100) / maxRightVelocity
  )
}
