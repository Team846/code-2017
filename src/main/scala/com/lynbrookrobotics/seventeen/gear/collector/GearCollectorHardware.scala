package com.lynbrookrobotics.seventeen.gear.collector

import edu.wpi.first.wpilibj.Solenoid

case class GearCollectorHardware(leftCylinder: Solenoid, rightCylinder:Solenoid)

object GearCollectorHardware{
  def apply(config: GearCollectorConfig): GearCollectorHardware = {
    GearCollectorHardware(new Solenoid(config.port.leftCylinder), new Solenoid(config.port.rightCylinder))
  }
}
