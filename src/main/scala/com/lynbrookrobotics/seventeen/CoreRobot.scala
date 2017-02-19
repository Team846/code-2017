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
//import com.lynbrookrobotics.potassium.commons.Point
import com.lynbrookrobotics.potassium.lighting.{DisplayLighting, LightingComponent, TwoWayComm}
import com.lynbrookrobotics.seventeen.lighting.SerialComms
import edu.wpi.first.wpilibj.SerialPort
import com.lynbrookrobotics.potassium.frc.Implicits._
import com.lynbrookrobotics.potassium.frc.SPIWrapper
import com.lynbrookrobotics.potassium.sensors.imu.ADIS16448
import com.lynbrookrobotics.potassium.sensors.position.xyPosition
import com.lynbrookrobotics.potassium.units.Point
import com.lynbrookrobotics.seventeen.drivetrain._
import edu.wpi.first.wpilibj.SPI
import com.lynbrookrobotics.potassium.units.Point
import squants.time.Milliseconds
import com.lynbrookrobotics.seventeen.drivetrain._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CoreRobot(configFileValue: Signal[String], updateConfigFile: String => Unit)
               (implicit config: Signal[RobotConfig], hardware: RobotHardware, clock: Clock, polling: ImpulseEvent) {
  lazy val ds = hardware.driver.station

  implicit val driverHardware = hardware.driver
  implicit val drivetrainHardware = hardware.drivetrain

  implicit val drivetrainProps = config.map(_.drivetrain.properties)

  implicit val drivetrain = new Drivetrain

  lazy val serialPort = try {
    Option(new SerialPort(9600, SerialPort.Port.kUSB))
  } catch {
    case e: Exception => None
  }

//  val comms = new SerialComms(serialPort.get)
//  val lighting = new LightingComponent(20, comms)

  val components = List(drivetrain/*, lighting*/)


//  driverHardware.operatorJoystick.buttonPressed(1).foreach(new DisplayLighting(Signal.constant(1) ,lighting))

  val disabled = Signal(ds.isDisabled).filter(identity)
  disabled.foreach(() => {
    drivetrainHardware.imu.calibrateUpdate()
  })

  val zero = new Point(Feet(0), Feet(0))

  val auto = Signal(ds.isEnabled && ds.isAutonomous).filter(identity)

  val target = drivetrainHardware.position.get + new Point(Feet(3), Feet(3))
  auto.foreach(new unicycleTasks.GoToPoint(target, Feet(0.1)).toContinuous)

  val enabled = Signal(ds.isEnabled).filter(identity)

  enabled.foreach(() => drivetrainHardware.imu.angleUpdate())


  val dashboard = Future {
    implicit val system = ActorSystem("funky-dashboard")

    implicit val materializer = ActorMaterializer()

    val dashboard = new FunkyDashboard

    Http().bindAndHandle(Route.handlerFlow(dashboard.route), "0.0.0.0", 8080).map { _ =>
      println("Funky Dashboard is up!")
      dashboard
    }
  }.flatten

  dashboard.onFailure{
    case  e => e.printStackTrace()
  }

  dashboard.foreach { board =>
    board.datasetGroup("Config").addDataset(new JsonEditor("Robot Config")(
      configFileValue.get,
      updateConfigFile
    ))

    board.datasetGroup("Power").addDataset(new TimeSeriesNumeric("Battery Voltage")(
      ds.getBatteryVoltage
    ))

//    board.datasetGroup("Drivetrain").addDataset(new TimeSeriesNumeric("Left Ground Speed")(
//      drivetrainHardware.leftVelocity.get.toFeetPerSecond
//    ))
//
//    board.datasetGroup("Drivetrain").addDataset(new TimeSeriesNumeric("Right Ground Speed")(
//      drivetrainHardware.rightVelocity.get.toFeetPerSecond
//    ))
//
//    board.datasetGroup("Drivetrain").addDataset(new TimeSeriesNumeric("Turn Velocity")(
//      drivetrainHardware.turnVelocity.get.toDegreesPerSecond
//    ))
//
//    board.datasetGroup("Drivetrain").addDataset(new TimeSeriesNumeric("Rotational Position")(
//        drivetrainHardware.turnPosition.get.toDegrees
//    ))

//    board.datasetGroup("Gyro").addDataset(new TimeSeriesNumeric("Gyro-X") (
//        drivetrainHardware.anglePose.get.map(_.x.toDegrees)
//    ))
//
//    board.datasetGroup("Gyro").addDataset(new TimeSeriesNumeric("Gyro-Y") (
//        drivetrainHardware.anglePose.get.map(_.y.toDegrees).getOrElse(0)
//    ))
//
//    board.datasetGroup("Gyro").addDataset(new TimeSeriesNumeric("Gyro-Z") (
//        drivetrainHardware.anglePose.get.map(_.z.toDegrees).getOrElse(0)
//    ))

    board.datasetGroup("Drivetrain").addDataset(new TimeSeriesNumeric("position-X") (
      try {
        drivetrainHardware.position.get.x.toFeet
      } catch {
        case e: Exception => -100.0
      }
    ))

    board.datasetGroup("Drivetrain").addDataset(new TimeSeriesNumeric("position-Y") (
      try {
        drivetrainHardware.position.get.y.toFeet
      } catch {
        case e: Exception => -200
      }
    ))

    board.datasetGroup("Drivetrain").addDataset(new TimeSeriesNumeric("position-Z") (
      try {
        drivetrainHardware.anglePose.get.z.toDegrees
      } catch {
        case e: Exception => -300.0
      }
    ))
  }


  enabled.onEnd.foreach { () =>
    components.foreach(_.resetToDefault())
  } // coment to enable telop, but disable auto

  enabled.onStart.foreach { () =>
    components.foreach(_.resetToDefault())
  }

}
