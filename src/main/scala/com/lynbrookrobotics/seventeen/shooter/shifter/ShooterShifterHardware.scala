package com.lynbrookrobotics.seventeen.shooter.shifter

import edu.wpi.first.wpilibj.Solenoid

case class ShooterShifterHardware(pneumatic: Solenoid)

object ShooterShifterHardware {
  def apply(config: ShooterShifterConfig): ShooterShifterHardware = {
    ShooterShifterHardware(new Solenoid(config.ports.pneumatic))
  }
}
