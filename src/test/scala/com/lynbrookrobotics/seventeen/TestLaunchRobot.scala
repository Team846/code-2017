package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.JavaClock
import com.lynbrookrobotics.potassium.config.TwoWaySignal
import com.lynbrookrobotics.potassium.events.ImpulseEventSource
import upickle.default._
import com.lynbrookrobotics.potassium.config.SquantsPickling._

object TestLaunchRobot extends App {
  protected val configFile = TwoWaySignal[String]("")
  configFile.value = "{\"driver\":{\"driverPort\":0,\"operatorPort\":1,\"driverWheelPort\":2},\"drivetrain\":{\"ports\":{\"leftBack\":4,\"leftFront\":3,\"rightBack\":0,\"rightFront\":1},\"properties\":{\"maxLeftVelocity\":[22.9,\"ft/s\"],\"maxRightVelocity\":[27,\"ft/s\"]}}}"
  protected val parsedConfig = configFile.map(string => read[RobotConfig](string))(
    (_, newValue) => write(newValue)
  )

  private implicit val config = Signal(parsedConfig.value)

  private implicit val hardware = null

  private val eventPollingSource = new ImpulseEventSource
  private implicit val eventPolling = eventPollingSource.event

  implicit val clock = JavaClock

  private val coreRobot = new CoreRobot(
    Signal(configFile.value),
    newS => {
      val oldS = configFile.value
      try {
        configFile.value = newS
      } catch {
        case _ => configFile.value = oldS
      }
    }
  )

  while (true) {
    println(parsedConfig.value)
    Thread.sleep(1000)
  }
}
