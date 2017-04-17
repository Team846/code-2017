package com.lynbrookrobotics.seventeen.gear.grabber

import com.lynbrookrobotics.potassium.frc.ProximitySensor
import edu.wpi.first.wpilibj.Solenoid

case class GearGrabberHardware(pneumatic: Solenoid,
                               proximitySensor: ProximitySensor)

object GearGrabberHardware {
  def apply(config: GearGrabberConfig): GearGrabberHardware = {
    GearGrabberHardware(
      pneumatic = new Solenoid(config.port.pneumatic),
      proximitySensor = new ProximitySensor(config.port.proximitySensor)
    )
  }
}
