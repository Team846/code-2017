package com.lynbrookrobotics.seventeen.drivetrain

import com.lynbrookrobotics.potassium.commons.drivetrain._
import squants.Length
import squants.motion.{RadiansPerSecond, Velocity, Acceleration}
import squants.space.Inches
import squants.time.Seconds

case class DrivetrainProperties(maxLeftVelocity: Velocity, maxRightVelocity: Velocity,
                                maxAcceleration: Acceleration,
                                wheelDiameter: Length, track: Length, gearRatio: Double,
                                turnControlGains: TurnVelocityGains,
                                forwardPositionControlGains: ForwardPositionGains,
                                turnPositionControlGains: TurnPositionGains,
                                leftControlGains: ForwardVelocityGains,
                                rightControlGains: ForwardVelocityGains) extends TwoSidedDriveProperties {
  override val maxTurnVelocity = RadiansPerSecond((((maxLeftVelocity + maxRightVelocity) * Seconds(1)) / Inches(21.75)) / 2)
}
