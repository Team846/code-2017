package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.seventeen.config._
import edu.wpi.first.wpilibj.IterativeRobot

import com.lynbrookrobotics.potassium.frc.Implicits._

class LaunchRobot extends IterativeRobot {
  implicit val config = RobotConfig(
    DriverConfig(
      operatorPort = 1
    )
  )

  private var coreRobot: CoreRobot = null

  override def robotInit(): Unit = {
    coreRobot = new CoreRobot
  }
}
