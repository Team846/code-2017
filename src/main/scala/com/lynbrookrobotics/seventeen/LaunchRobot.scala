package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.seventeen.config._
import edu.wpi.first.wpilibj.{DriverStation, RobotBase}
import com.lynbrookrobotics.potassium.frc.Implicits._
import edu.wpi.first.wpilibj.hal.HAL

class LaunchRobot extends RobotBase {
  implicit val config = RobotConfig(
    DriverConfig(
      operatorPort = 1
    )
  )

  private var coreRobot: CoreRobot = null

  val ds = DriverStation.getInstance()

  override def startCompetition(): Unit = {
    HAL.observeUserProgramStarting()

    coreRobot = new CoreRobot

    while (true) {
      ds.waitForData()
    }
  }
}
