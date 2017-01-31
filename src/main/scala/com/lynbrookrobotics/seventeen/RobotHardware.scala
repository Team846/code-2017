package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.seventeen.driver.DriverHardware
import com.lynbrookrobotics.seventeen.drivetrain.DrivetrainHardware

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