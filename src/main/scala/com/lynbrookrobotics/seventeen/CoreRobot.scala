package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.seventeen.agitator.Agitator
import com.lynbrookrobotics.seventeen.camselect.CamSelect
import com.lynbrookrobotics.seventeen.climber.puller.ClimberPuller
import com.lynbrookrobotics.seventeen.collector.elevator.CollectorElevator
import com.lynbrookrobotics.seventeen.collector.extender.CollectorExtender
import com.lynbrookrobotics.seventeen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.seventeen.gear.grabber.GearGrabber
import com.lynbrookrobotics.seventeen.gear.tilter.GearTilter
import com.lynbrookrobotics.seventeen.shooter.flywheel.ShooterFlywheel
import com.lynbrookrobotics.seventeen.shooter.shifter.{ShooterShiftLeft, ShooterShiftRight, ShooterShifter}
import com.lynbrookrobotics.potassium.{Component, Signal}
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.events.ImpulseEvent
import com.lynbrookrobotics.funkydashboard.{FunkyDashboard, JsonEditor, TimeSeriesNumeric}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.lynbrookrobotics.potassium.lighting.LightingComponent
import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.tasks.FiniteTask
import com.lynbrookrobotics.potassium.units.Point
import com.lynbrookrobotics.seventeen.drivetrain._
import com.lynbrookrobotics.seventeen.lighting.{SerialComms, StatusLightingComponent}
import edu.wpi.first.wpilibj.DriverStation.Alliance
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj.{Compressor, PowerDistributionPanel, SerialPort}
import squants.space.{Feet, Inches}

class CoreRobot(configFileValue: Signal[String], updateConfigFile: String => Unit)
               (implicit val config: Signal[RobotConfig], hardware: RobotHardware, val clock: Clock, val polling: ImpulseEvent) {
  implicit val driverHardware = hardware.driver
  val ds = driverHardware.station

  // Drivetrain
  implicit val drivetrainHardware = hardware.drivetrain
  implicit val drivetrainProps = config.map(_.drivetrain.properties)
  lazy val drivetrain: Option[Drivetrain] =
    if (config.get.drivetrain != null) Some(new Drivetrain) else None

  // Agitator
  implicit val agitatorHardware = hardware.agitator
  implicit val agitatorProps = config.map(_.agitator.properties)
  lazy val agitator: Option[Agitator] =
    if (config.get.agitator != null) Some(new Agitator) else None

  // CamSelect
  implicit val camSelectHardware = hardware.camSelect
  implicit val camselectProps = config.map(_.camSelect.properties)
  implicit val camSelect: CamSelect = new CamSelect

  // Climber Puller
  implicit val climberPullerHardware = hardware.climberPuller
  implicit val climberPullerProps = config.map(_.climberPuller.props)
  lazy val climberPuller: Option[ClimberPuller] =
    if (config.get.climberPuller != null) Some(new ClimberPuller) else None

  // Collector Elevator
  implicit val collectorElevatorHardware = hardware.collectorElevator
  implicit val collectorElevatorProps = config.map(_.collectorElevator.properties)
  lazy val collectorElevator: Option[CollectorElevator] =
    if (config.get.collectorElevator != null) Some(new CollectorElevator) else None

  // Collector Extender
  implicit val collectorExtenderHardware = hardware.collectorExtender
  lazy val collectorExtender: Option[CollectorExtender] =
    if (config.get.collectorExtender != null) {
      implicit val gt = () => gearTilter
      Some(new CollectorExtender)
    } else None

  // Collector Rollers
  implicit val collectorRollersHardware = hardware.collectorRollers
  implicit val collectorRollersProps = config.map(_.collectorRollers.properties)
  lazy val collectorRollers: Option[CollectorRollers] =
    if (config.get.collectorRollers != null) Some(new CollectorRollers) else None

  // Gear Grabber
  implicit val gearGrabberHardware = hardware.gearGrabber
  implicit val gearGrabberProps = config.map(_.gearGrabber.props)
  lazy val gearGrabber: Option[GearGrabber] =
    if (config.get.gearGrabber != null) Some(new GearGrabber) else None

  // Gear Tilter
  implicit val gearTilterHardware = hardware.gearTilter
  lazy val gearTilter: Option[GearTilter] =
    if (config.get.gearTilter != null) {
      implicit val ce = () => collectorExtender
      Some(new GearTilter)
    } else None

  // Shooter Flywheel
  implicit val shooterFlywheelHardware = hardware.shooterFlywheel
  implicit val shooterFlywheelProps = config.map(_.shooterFlywheel.props)
  lazy val shooterFlywheel: Option[ShooterFlywheel] =
    if (config.get.shooterFlywheel != null) Some(new ShooterFlywheel) else None

  // Shooter Shifter
  implicit val shooterShifterHardware = hardware.shooterShifter
  lazy val shooterShifter: Option[ShooterShifter] =
    if (config.get.shooterShifter != null) Some(new ShooterShifter) else None

  // Lighting
  /**
    * Function to determine what lighting effect should be displayed
    */
  val lightingStatus: () => Int = () => {
    val gearState = gearGrabber.isDefined && gearGrabberHardware.proximitySensor.getVoltage > gearGrabberProps.get.detectingDistance.value
    if (ds.getBatteryVoltage < 12){
      12
    } else if (climberPuller.isDefined && climberPullerHardware.motorA.get() > 0){
      9
    } else if (gearState) {
      5
    } else if (ds.getMatchTime >= 135){
      16
    } else if (ds.isDisabled){
      1
    } else if (ds.isAutonomous){
      if (ds.getAlliance == Alliance.Blue){
        8
      } else {
        10
      }
    } else if(shooterFlywheel.isDefined && shooterFlywheelHardware.leftVelocity.get.toHertz > 0 ) {
      7
    } else {
      0
    }
  }

  val serialPort = Try(new SerialPort(9600, SerialPort.Port.kUSB)).toOption
  val comms: Option[SerialComms] = serialPort.map(new SerialComms(_))
  val lighting: Option[StatusLightingComponent] = comms.map(c => new StatusLightingComponent(lightingStatus, c))

  new Compressor().start()

  val components: List[Component[_]] = List(
    drivetrain,
    agitator,
    climberPuller,
    collectorElevator,
    collectorExtender,
    collectorRollers,
    gearGrabber,
    gearTilter,
    shooterFlywheel,
    shooterShifter,
    lighting
  ).flatten

  new ButtonMappings(this)

  val auto = Signal(ds.isEnabled && ds.isAutonomous).filter(identity)

  val generator = new AutoGenerator(this)

  auto.foreach(Signal {
    val autoID = Math.round(SmartDashboard.getNumber("DB/Slider 0"))

    (if (autoID == 1) {
      drivetrain.flatMap { implicit dr =>
        gearGrabber.map { implicit d =>
//          generator.centerGearAndCrossLine
          FiniteTask.empty
        }
      }.getOrElse(FiniteTask.empty)
    } else if (autoID == 2) {
      drivetrain.flatMap { implicit dr =>
        gearGrabber.flatMap { implicit d =>
          collectorElevator.flatMap { implicit ce =>
            collectorRollers.flatMap { implicit cr =>
              agitator.flatMap { implicit a =>
                shooterFlywheel.map { implicit f =>
//                  generator.shootCenterGearAndCrossLine
                  FiniteTask.empty
                }
              }
            }
          }
        }
      }.getOrElse(FiniteTask.empty)
    } else if (autoID == 3) {
      drivetrain.flatMap { implicit dr =>
        gearGrabber.flatMap { implicit d =>
          collectorElevator.flatMap { implicit ce =>
            collectorRollers.flatMap { implicit cr =>
              agitator.flatMap { implicit a =>
                shooterFlywheel.map { implicit f =>
                  generator.slowCrossLine
                }
              }
            }
          }
        }
      }.getOrElse(FiniteTask.empty)
    } else {
      FiniteTask.empty
    }).toContinuous
  })

  // Needs to go last because component resets have highest priority
  val enabled = Signal(ds.isEnabled).filter(identity)
  enabled.onStart.foreach { () =>
    if (drivetrainHardware != null) {
      drivetrainHardware.gyro.endCalibration()
    }

    components.foreach(_.resetToDefault())
  }

  enabled.onEnd.foreach { () =>
    components.foreach(_.resetToDefault())
  }

  val dashboard = Future {
    implicit val system = ActorSystem(
      "funky-dashboard",
      ConfigFactory.load("dashboard.conf")
    )

    implicit val materializer = ActorMaterializer()

    val dashboard = new FunkyDashboard

    Http().bindAndHandle(Route.handlerFlow(dashboard.route), "0.0.0.0", 8080).map { _ =>
      println("Funky Dashboard is up!")
      dashboard
    }
  }.flatten

  dashboard.failed.foreach(_.printStackTrace())

  dashboard.foreach { board =>
    board.datasetGroup("Config").addDataset(new JsonEditor("Robot Config")(
      configFileValue.get,
      updateConfigFile
    ))

    board.datasetGroup("Power").addDataset(new TimeSeriesNumeric("Battery Voltage")(
      ds.getBatteryVoltage
    ))

    board.datasetGroup("Joysticks").addDataset(new TimeSeriesNumeric("POV")(
      driverHardware.operatorJoystick.getPOV()
    ))

    drivetrain.foreach { d =>
      board.datasetGroup("Drivetrain").addDataset(new TimeSeriesNumeric("Left Ground Speed")(
        drivetrainHardware.leftVelocity.get.toFeetPerSecond
      ))

      board.datasetGroup("Drivetrain").addDataset(new TimeSeriesNumeric("Right Ground Speed")(
        drivetrainHardware.rightVelocity.get.toFeetPerSecond
      ))

      board.datasetGroup("Drivetrain").addDataset(new TimeSeriesNumeric("Left Wheel Rotation")(
        (drivetrainHardware.leftEncoder.angle.get * drivetrainProps.get.gearRatio).toDegrees
      ))

      board.datasetGroup("Drivetrain").addDataset(new TimeSeriesNumeric("Right Wheel Rotation")(
        (drivetrainHardware.rightEncoder.angle.get * drivetrainProps.get.gearRatio).toDegrees
      ))

      board.datasetGroup("Drivetrain").addDataset(new TimeSeriesNumeric("Turn Velocity")(
        drivetrainHardware.turnVelocity.get.toDegreesPerSecond
      ))

      board.datasetGroup("Drivetrain").addDataset(new TimeSeriesNumeric("Rotational Position")(
        drivetrainHardware.turnPosition.get.toDegrees
      ))
    }

    shooterFlywheel.foreach { d =>
      board.datasetGroup("Flywheel").addDataset(new TimeSeriesNumeric("Left Speed")(
        shooterFlywheelHardware.leftVelocity.get.toRevolutionsPerMinute
      ))

      board.datasetGroup("Flywheel").addDataset(new TimeSeriesNumeric("Right Speed")(
        shooterFlywheelHardware.rightVelocity.get.toRevolutionsPerMinute
      ))

      board.datasetGroup("Flywheel").addDataset(new TimeSeriesNumeric("Left Out")(
        shooterFlywheelHardware.leftMotor.get()
      ))

      board.datasetGroup("Flywheel").addDataset(new TimeSeriesNumeric("Right Out")(
        shooterFlywheelHardware.rightMotor.get()
      ))

      board.datasetGroup("Flywheel").addDataset(new TimeSeriesNumeric("Right - Left")(
        (shooterFlywheelHardware.rightVelocity.get - shooterFlywheelHardware.leftVelocity.get)
          .toRevolutionsPerMinute
      ))
    }

    climberPuller.foreach { c =>
      board.datasetGroup("Climber").addDataset(new TimeSeriesNumeric("Motor A")(
        hardware.pdp.getCurrent(3)
      ))

      board.datasetGroup("Climber").addDataset(new TimeSeriesNumeric("Motor B")(
        hardware.pdp.getCurrent(2)
      ))
    }

    gearGrabber.foreach { g =>
      board.datasetGroup("Grabber").addDataset(new TimeSeriesNumeric("IR Distance")(
        hardware.gearGrabber.proximitySensor.getVoltage
      ))
    }
  }
}
