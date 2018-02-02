package com.lynbrookrobotics.seventeen.drivetrain

import com.lynbrookrobotics.potassium.commons.drivetrain._
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSidedDriveProperties
import com.lynbrookrobotics.potassium.units.Ratio
import squants.motion.{Acceleration, RadiansPerSecond, Velocity}
import squants.space.Inches
import squants.time.Seconds
import squants.{Dimensionless, Each, Length, Percent}

case class DrivetrainProperties(maxLeftVelocity: Velocity, maxRightVelocity: Velocity,
                                maxAcceleration: Acceleration,
                                wheelDiameter: Length, track: Length, gearRatio: Double,
                                turnVelocityGains: TurnVelocityGains,
                                forwardPositionGains: ForwardPositionGains,
                                turnPositionGains: TurnPositionGains,
                                leftVelocityGains: ForwardVelocityGains,
                                rightVelocityGains: ForwardVelocityGains,
                                currentLimit: Dimensionless,
                                defaultLookAheadDistance: Length,
                                blendExponent: Double,
                                robotLength: Length) extends TwoSidedDriveProperties {
  override val maxTurnVelocity = RadiansPerSecond((((maxLeftVelocity + maxRightVelocity) * Seconds(1)) / Inches(21.75)) / 2)

  val maxCurvature = Ratio(
    num = Each(Int.MaxValue),
    den = track / 2d)
}
