package com.lynbrookrobotics.seventeen.gear.grabber

import edu.wpi.first.wpilibj.Solenoid

case class GearGrabberHardware(pneumatic: Solenoid)

object GearGrabberHardware{
  def apply(config: GearGrabberConfig): GearGrabberHardware = {
    GearGrabberHardware(new Solenoid(config.port.pneumatic))
  }
}
