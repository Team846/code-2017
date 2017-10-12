package com.lynbrookrobotics.seventeen.gear.roller

import com.ctre.CANTalon

case class GearRollerHardware(motor: CANTalon)

object GearRollerHardware {
  def apply(config: GearRollerConfig): GearRollerHardware = {
    GearRollerHardware(
      motor = new CANTalon(config.ports.motor)
    )
  }
}
