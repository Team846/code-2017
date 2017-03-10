package com.lynbrookrobotics.seventeen

import java.io.File

import com.google.common.reflect.ClassPath
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.config.TwoWayFile
import com.lynbrookrobotics.potassium.events.ImpulseEventSource
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.hal.HAL
import com.lynbrookrobotics.potassium.frc.Implicits._
import upickle.default._
import com.lynbrookrobotics.potassium.config.SquantsPickling._

class LaunchRobot extends RobotBase {
  protected val configFile = new TwoWayFile(new File("/home/lvuser/robot-config.json"))
  protected val parsedConfig = configFile.map(string => read[RobotConfig](string))(
    (_, newValue) => write(newValue)
  )

  private implicit val config = Signal(parsedConfig.value)

  private implicit val hardware = RobotHardware(config.get)

  private var coreRobot: CoreRobot = null

  private val ds = m_ds

  private val eventPollingSource = new ImpulseEventSource
  private implicit val eventPolling = eventPollingSource.event

  override def startCompetition(): Unit = {
    coreRobot = new CoreRobot(
      Signal(configFile.value),
      newS => {
        println(newS)
        val oldS = configFile.value
        try {
          configFile.value = newS
        } catch {
          case _ => configFile.value = oldS
        }
      }
    )

    ClassPath.from(Thread.currentThread().getContextClassLoader).
      getTopLevelClassesRecursive("com.lynbrookrobotics").
      forEach(c => println(s"preloaded ${c.getName}"))

    ClassPath.from(Thread.currentThread().getContextClassLoader).
      getTopLevelClassesRecursive("squants").
      forEach(c => println(s"preloaded ${c.getName}"))

    ClassPath.from(Thread.currentThread().getContextClassLoader).
      getTopLevelClassesRecursive("edu.wpi.first.wpilibj").
      forEach(c => println(s"preloaded ${c.getName}"))

    ClassPath.from(Thread.currentThread().getContextClassLoader).
      getTopLevelClassesRecursive("com.ctre").
      forEach(c => println(s"preloaded ${c.getName}"))

    coreRobot.comms.foreach(_.connect())

    WakeOnLan.awaken("B8:AE:ED:7E:78:E1")

    HAL.observeUserProgramStarting()

    while (true) {
      ds.waitForData()
      eventPollingSource.fire()
    }
  }
}
