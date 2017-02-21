package com.lynbrookrobotics.seventeen.shooter.flywheel

import com.lynbrookrobotics.potassium.commons.flywheel.DoubleFlywheelProperties
import com.lynbrookrobotics.potassium.control.PIDFConfig
import com.lynbrookrobotics.potassium.units.{GenericDerivative, GenericValue}
import squants.{Angle, Dimensionless}
import squants.motion.AngularVelocity

case class ShooterFlywheelProperties(velocityGains: PIDFConfig[AngularVelocity,
                                                    GenericValue[AngularVelocity],
                                                    AngularVelocity,
                                                    GenericDerivative[AngularVelocity],
                                                    Angle,
                                                    Dimensionless],
                                     lowShootSpeed: AngularVelocity,
                                     midShootSpeed: AngularVelocity,
                                     fastShootSpeed: AngularVelocity,
                                     speedTolerance: AngularVelocity) extends DoubleFlywheelProperties
