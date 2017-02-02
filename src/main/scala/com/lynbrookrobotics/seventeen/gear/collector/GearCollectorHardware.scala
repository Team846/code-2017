package com.lynbrookrobotics.seventeen.gear.collector

import edu.wpi.first.wpilibj.Solenoid

case class GearCollectorHardware(pneumatic: Solenoid)

object GearCollectorHardware{
  def apply(config: GearCollectorConfig): GearCollectorHardware = {
    GearCollectorHardware(new Solenoid(config.port.pneumatic))
  }
}
