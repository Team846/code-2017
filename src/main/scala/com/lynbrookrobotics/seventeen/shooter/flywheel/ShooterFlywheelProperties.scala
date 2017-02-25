package com.lynbrookrobotics.seventeen.shooter.flywheel

import com.lynbrookrobotics.potassium.commons.flywheel.DoubleFlywheelProperties
import com.lynbrookrobotics.potassium.control.PIDConfig
import com.lynbrookrobotics.potassium.units.{GenericDerivative, GenericValue}
import squants.Dimensionless
import squants.time.Frequency

case class ShooterFlywheelProperties(maxVelocityLeft: Frequency,
                                     maxVelocityRight: Frequency,
                                     velocityGainsLeft: PIDConfig[Frequency,
                                                                  GenericValue[Frequency],
                                                                  Frequency,
                                                                  GenericDerivative[Frequency],
                                                                  Dimensionless,
                                                                  Dimensionless],
                                     velocityGainsRight: PIDConfig[Frequency,
                                                                   GenericValue[Frequency],
                                                                   Frequency,
                                                                   GenericDerivative[Frequency],
                                                                   Dimensionless,
                                                                   Dimensionless],
                                     lowShootSpeed: Frequency,
                                     midShootSpeed: Frequency,
                                     fastShootSpeed: Frequency,
                                     speedTolerance: Frequency) extends DoubleFlywheelProperties
