package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.JavaClock
import com.lynbrookrobotics.potassium.config.TwoWaySignal
import com.lynbrookrobotics.potassium.events.ImpulseEventSource
import com.lynbrookrobotics.potassium.streams.Stream
import upickle.default._
import com.lynbrookrobotics.potassium.config.SquantsPickling._
import squants.time.Seconds

import scala.io.Source

object TestLaunchRobot extends App {
  protected val configFile = TwoWaySignal[String]("")
  configFile.value = Source.fromFile("competition-robot.json").getLines().mkString("\n")
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
        case _: Throwable => configFile.value = oldS
      }
    },
    Stream.periodic(Seconds(0.01))(())
  )
}
