package com.lynbrookrobotics.seventeen.gear.roller

import com.ctre.CANTalon

case class GearRollerHardware(motor: CANTalon)

object GearRollerHardware {
  def apply(config: GearRollerConfig): GearRollerHardware = {
    GearRollerHardware(
      motor = {
        val talon = new CANTalon(config.ports.motor)
        talon.setInverted(true)
        talon
      }
    )
  }
}
