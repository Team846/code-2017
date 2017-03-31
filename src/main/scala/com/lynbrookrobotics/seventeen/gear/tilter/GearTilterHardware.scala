package com.lynbrookrobotics.seventeen.gear.tilter

import edu.wpi.first.wpilibj.Solenoid

case class GearTilterHardware(pneumatic: Solenoid)

object GearTilterHardware {
  def apply(config: GearTilterConfig): GearTilterHardware = {
    GearTilterHardware(new Solenoid(config.port.pneumatic))
  }
}
