package com.lynbrookrobotics.seventeen.hardware

import com.lynbrookrobotics.seventeen.config.DriverConfig
import edu.wpi.first.wpilibj.Joystick

case class DriverHardware(operatorJoystick: Joystick)

object DriverHardware {
  def apply(config: DriverConfig): DriverHardware = {
    DriverHardware(new Joystick(config.operatorPort))
  }
}