package com.lynbrookrobotics.seventeen.gear.roller

import squants.Dimensionless
import squants.electro.{ElectricCurrent, ElectricPotential}

case class GearRollerProperties(defaultHoldingPower: Dimensionless,
                                intakeGearPower: Dimensionless, emitGearPower: Dimensionless,
                                gearDetectionCurrent: ElectricCurrent)