package com.lynbrookrobotics.seventeen

import java.io.File

import com.google.common.reflect.ClassPath
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.config.TwoWayFile
import com.lynbrookrobotics.potassium.events.ImpulseEventSource
import com.lynbrookrobotics.potassium.frc.Implicits._
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.hal.HAL
import upickle.default._
import com.lynbrookrobotics.potassium.config.SquantsPickling._
import com.lynbrookrobotics.potassium.frc.WPIClock
import squants.Percent
import com.lynbrookrobotics.potassium.streams.Stream
import squants.time.Seconds

class LaunchRobot extends RobotBase {
  val targetFile = new File("/home/lvuser/robot-config.json")
  if (!targetFile.exists()) {
    targetFile.createNewFile()
  }

  protected val configFile = new TwoWayFile(targetFile)
  protected val parsedConfig = configFile.map { string =>
    val ret: RobotConfig = try {
      read[RobotConfig](string)
    } catch {
      case e: Throwable =>
        println(s"Exception when reading config: $e")
        read[RobotConfig](DefaultConfig.defaultConfig)
    }

    ret
  }(
    (_, newValue) => write(newValue)
  )

  private implicit val config = Signal {
    parsedConfig.value
  }

  implicit val clock = WPIClock

  private implicit val hardware = RobotHardware(config.get)

  private var coreRobot: CoreRobot = null

  private val ds = m_ds

  private val eventPollingSource = new ImpulseEventSource
  private implicit val eventPolling = eventPollingSource.event

  private val classpath = ClassPath.from(Thread.currentThread().getContextClassLoader)
  def preload(pkg: String): Unit = {
    classpath.getTopLevelClassesRecursive(pkg)
    println(s"Preloaded $pkg")
  }

  override def startCompetition(): Unit = {
    WakeOnLan.awaken("B8:AE:ED:7E:78:E1")

    coreRobot = new CoreRobot(
      Signal(configFile.value),
      newS => {
        val oldS = configFile.value
        try {
          configFile.value = newS
        } catch {
          case e: Throwable =>
            println(s"Unable to set new value for config, exception $e")
            configFile.value = oldS
        }
      },
      Stream.periodic(Seconds(0.01))(())
    )

    println("Preloading")
    preload("com.lynbrookrobotics")
    preload("squants")
    preload("edu.wpi.first.wpilibj")
    preload("com.ctre")

    coreRobot.comms.foreach(_.connect())

    HAL.observeUserProgramStarting()

    println("------------------------------------------\n" +
      "Finished preloading and establishing connections. " +
      "Wait 5 seconds to allow for sensor calibration\n")

    while (true) {
      ds.waitForData()
      eventPollingSource.fire()
      coreRobot.driverHardware.driverStationUpdate.apply()
    }
  }
}
