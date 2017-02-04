package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.events.ImpulseEventSource
import com.lynbrookrobotics.seventeen.driver._
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.hal.HAL
import squants.motion.FeetPerSecond
import com.lynbrookrobotics.potassium.frc.Implicits.clock
import com.lynbrookrobotics.seventeen.drivetrain.{DrivetrainConfig, DrivetrainPorts, DrivetrainProperties}

class LaunchRobot extends RobotBase {
  private implicit val config = Signal.constant(RobotConfig(
    DriverConfig(
      driverPort = 0,
      operatorPort = 1,
      driverWheelPort = 2
    ),
    DrivetrainConfig(
      ports = DrivetrainPorts(
        leftBack = 4,
        leftFront = 3,
        rightBack = 0,
        rightFront = 1
      ),
      properties = DrivetrainProperties(
        maxLeftVelocity = FeetPerSecond(22.9),
        maxRightVelocity = FeetPerSecond(27)
      )
    )
  ))

  private implicit val hardware = RobotHardware(config.get)

  private var coreRobot: CoreRobot = null

  private val ds = m_ds

  private val eventPollingSource = new ImpulseEventSource
  private implicit val eventPolling = eventPollingSource.event

  override def startCompetition(): Unit = {
    coreRobot = new CoreRobot

    HAL.observeUserProgramStarting()

    while (true) {
      ds.waitForData()
      eventPollingSource.fire()
    }
  }
}
