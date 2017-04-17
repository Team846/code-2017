package com.lynbrookrobotics.seventeen.loadtray

import edu.wpi.first.wpilibj.Solenoid

case class LoadTrayHardware(pneumatic: Solenoid)

object LoadTrayHardware {
  def apply(config: LoadTrayConfig): LoadTrayHardware = {
    LoadTrayHardware(new Solenoid(config.port.pneumatic))
  }
}
