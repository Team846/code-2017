package com.lynbrookrobotics.seventeen.hardware

import com.lynbrookrobotics.seventeen.config.RobotConfig

case class RobotHardware(driver: DriverHardware, drivetrain: DrivetrainHardware)

object RobotHardware {
  def apply(robotConfig: RobotConfig): RobotHardware = {
    val driver = DriverHardware(robotConfig.driver)
    RobotHardware(
      driver = driver,
      drivetrain = DrivetrainHardware(robotConfig.drivetrain, driver)
    )
  }
}