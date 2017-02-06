package com.lynbrookrobotics.seventeen.driver

import edu.wpi.first.wpilibj.{DriverStation, Joystick}

case class DriverHardware(driverJoystick: Joystick,
                          operatorJoystick: Joystick,
                          driverWheel: Joystick,
                          station: DriverStation)

object DriverHardware {
  def apply(config: DriverConfig): DriverHardware = {
    DriverHardware(
      new Joystick(config.driverPort),
      new Joystick(config.operatorPort),
      new Joystick(config.driverWheelPort),
      DriverStation.getInstance()
    )
  }
}