package com.lynbrookrobotics.seventeen

import java.io.{File, PrintWriter}

import com.google.common.reflect.ClassPath
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.config.SquantsPickling._
import com.lynbrookrobotics.potassium.events.ImpulseEventSource
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.streams.Stream
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.hal.HAL
import squants.time.Seconds
import upickle.default._

import scala.io.Source

class LaunchRobot extends RobotBase {
  val targetFile = new File("/home/lvuser/robot-config.json")
  if (!targetFile.exists()) {
    targetFile.createNewFile()
  }

  def writeConfigFileValue(v: String): Unit = {
    try {
      val writer = new PrintWriter(targetFile)
      writer.println(v)
      writer.close()
    } catch {
      case e: Throwable =>
        e.printStackTrace()
    }
  }

  private var latestConfigString: String = {
    try {
      Source.fromFile(targetFile).getLines().mkString("\n")
    } catch {
      case e: Throwable =>
        e.printStackTrace()
        writeConfigFileValue(DefaultConfig.defaultConfig)
        DefaultConfig.defaultConfig
    }
  }

  private var latestConfig: RobotConfig = {
    try {
      read[RobotConfig](latestConfigString)
    } catch {
      case e: Throwable =>
        e.printStackTrace()
        writeConfigFileValue(DefaultConfig.defaultConfig)
        latestConfigString = DefaultConfig.defaultConfig
        read[RobotConfig](latestConfigString)
    }
  }

  private implicit val config = Signal(latestConfig)

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
      Signal(latestConfigString),
      newS => {
        try {
          val readValue = read[RobotConfig](newS)
          val writtenValue = write[RobotConfig](readValue)
          writeConfigFileValue(writtenValue)
          latestConfigString = writtenValue
          latestConfig = readValue
        } catch {
          case e: Exception =>
            e.printStackTrace()
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

    println("------------------------------------------\n" +
      "Finished preloading and establishing connections. " +
      "Wait 5 seconds to allow for sensor calibration\n")

    Thread.sleep(5000)

    HAL.observeUserProgramStarting()

    while (true) {
      ds.waitForData()
      eventPollingSource.fire()
      coreRobot.driverHardware.driverStationUpdate.apply()
    }
  }
}
