package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.funkydashboard.{FunkyDashboard, JsonEditor, TimeSeriesNumeric}
import com.lynbrookrobotics.potassium.events.ImpulseEvent
import squants.space.{Degrees, Feet, Inches}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.lynbrookrobotics.potassium.lighting.{DisplayLighting, LightingComponent, TwoWayComm}
import com.lynbrookrobotics.seventeen.drivetrain._
import com.lynbrookrobotics.seventeen.lighting.SerialComms
import edu.wpi.first.wpilibj.SerialPort
import com.lynbrookrobotics.potassium.frc.Implicits._
import com.lynbrookrobotics.potassium.tasks.{ContinuousTask, Task}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CoreRobot(configFileValue: Signal[String], updateConfigFile: String => Unit)
               (implicit config: Signal[RobotConfig], hardware: RobotHardware, clock: Clock, polling: ImpulseEvent) {
  lazy val ds = hardware.driver.station

  implicit val driverHardware = hardware.driver
  implicit val drivetrainHardware = hardware.drivetrain

  implicit val drivetrainProps = config.map(_.drivetrain.properties)

  implicit val drivetrain = new Drivetrain

  lazy val serialPort = new SerialPort(9600, SerialPort.Port.kUSB)
  val comms = new SerialComms(serialPort)
  val lighting = new LightingComponent(20, comms)

  val components = List(drivetrain, lighting)

  driverHardware.operatorJoystick.buttonPressed(1).foreach(new DisplayLighting(Signal.constant(1) ,lighting))

  val enabled = Signal(ds.isEnabled).filter(identity)
  enabled.onStart.foreach { () =>
    components.foreach(_.resetToDefault())
  }

  enabled.onEnd.foreach { () =>
    components.foreach(_.resetToDefault())
  }

  val auto = Signal(ds.isEnabled && ds.isAutonomous).filter(identity)
  auto.foreach(new unicycleTasks.DriveDistance(
    Feet(2),
    Inches(2)
  ).then(new unicycleTasks.RotateByAngle(
    Degrees(180),
    Degrees(3)
  )).then(new unicycleTasks.DriveDistance(
    Feet(2),
    Inches(2)
  )).toContinuous)

  val dashboard = Future {
    implicit val system = ActorSystem("funky-dashboard")

    implicit val materializer = ActorMaterializer()

    val dashboard = new FunkyDashboard

    Http().bindAndHandle(Route.handlerFlow(dashboard.route), "0.0.0.0", 8080).map { _ =>
      println("Funky Dashboard is up!")
      dashboard
    }
  }.flatten

  dashboard.foreach { board =>
    board.datasetGroup("Config").addDataset(new JsonEditor("Robot Config")(
      configFileValue.get,
      updateConfigFile
    ))

    board.datasetGroup("Power").addDataset(new TimeSeriesNumeric("Battery Voltage")(
      ds.getBatteryVoltage
    ))

    board.datasetGroup("Drivetrain").addDataset(new TimeSeriesNumeric("Left Ground Speed")(
      drivetrainHardware.leftVelocity.get.toFeetPerSecond
    ))

    board.datasetGroup("Drivetrain").addDataset(new TimeSeriesNumeric("Right Ground Speed")(
      drivetrainHardware.rightVelocity.get.toFeetPerSecond
    ))

    board.datasetGroup("Drivetrain").addDataset(new TimeSeriesNumeric("Turn Velocity")(
      drivetrainHardware.turnVelocity.get.toDegreesPerSecond
    ))

    board.datasetGroup("Drivetrain").addDataset(new TimeSeriesNumeric("Rotational Position")(
      drivetrainHardware.turnPosition.get.toDegrees
    ))
  }
}
