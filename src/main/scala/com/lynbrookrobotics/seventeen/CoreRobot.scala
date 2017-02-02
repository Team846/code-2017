package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.seventeen.config.RobotConfig
import com.lynbrookrobotics.seventeen.hardware.RobotHardware
import com.lynbrookrobotics.seventeen.component.drivetrain.Drivetrain
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.funkydashboard.{FunkyDashboard, TimeSeriesNumeric}
import com.lynbrookrobotics.potassium.events.ImpulseEvent

import com.lynbrookrobotics.seventeen.component.drivetrain.unicycleTasks

import edu.wpi.first.wpilibj.DriverStation

import squants.space.{Degrees, Feet, Inches}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer


import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CoreRobot(implicit config: Signal[RobotConfig], hardware: RobotHardware, clock: Clock, polling: ImpulseEvent) {
  val ds = DriverStation.getInstance()

  implicit val driverHardware = hardware.driver
  implicit val drivetrainHardware = hardware.drivetrain

  implicit val drivetrainProps = config.map(_.drivetrain.properties)

  implicit val drivetrain = new Drivetrain

  val components = List(drivetrain)

  val teleop = Signal(ds.isEnabled && ds.isOperatorControl).filter(identity)
  teleop.onStart.foreach { () =>
    components.foreach(_.resetToDefault())
  }

  teleop.onEnd.foreach { () =>
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