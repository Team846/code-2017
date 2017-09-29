package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.events.ImpulseEvent
import com.lynbrookrobotics.potassium.tasks.{ContinuousTask, FiniteTask, Task}
import com.lynbrookrobotics.potassium.{Component, Signal}
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.seventeen.agitator.Agitator
import com.lynbrookrobotics.seventeen.camselect.CamSelect
import com.lynbrookrobotics.seventeen.climber.puller.ClimberPuller
import com.lynbrookrobotics.seventeen.collector.elevator.CollectorElevator
import com.lynbrookrobotics.seventeen.collector.extender.CollectorExtender
import com.lynbrookrobotics.seventeen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.seventeen.drivetrain._
import com.lynbrookrobotics.seventeen.gear.grabber.GearGrabber
import com.lynbrookrobotics.seventeen.gear.tilter.GearTilter
import com.lynbrookrobotics.seventeen.lighting.{SerialComms, StatusLightingComponent}
import com.lynbrookrobotics.seventeen.loadtray.LoadTray
import com.lynbrookrobotics.seventeen.shooter.flywheel.ShooterFlywheel
import com.lynbrookrobotics.seventeen.shooter.shifter.ShooterShifter
import com.lynbrookrobotics.funkydashboard.{FunkyDashboard, JsonEditor, TimeSeriesNumeric}
import edu.wpi.first.wpilibj._
import edu.wpi.first.wpilibj.DriverStation.Alliance
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard

import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import java.lang.Runtime

import scala.collection.mutable

class CoreRobot(configFileValue: Signal[String], updateConfigFile: String => Unit, val coreTicks: Stream[Unit])
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
    if (config.get.agitator != null) Some(new Agitator(coreTicks)) else None

  // CamSelect
  implicit val camSelectHardware = hardware.camSelect
  implicit val camselectProps = config.map(_.camSelect.properties)
  implicit val camSelect: CamSelect = new CamSelect(coreTicks)


  // Climber Puller
  implicit val climberPullerHardware = hardware.climberPuller
  implicit val climberPullerProps = config.map(_.climberPuller.props)
  lazy val climberPuller: Option[ClimberPuller] =
    if (config.get.climberPuller != null) Some(new ClimberPuller(coreTicks)) else None

  // Collector Elevator
  implicit val collectorElevatorHardware = hardware.collectorElevator
  implicit val collectorElevatorProps = config.map(_.collectorElevator.properties)
  lazy val collectorElevator: Option[CollectorElevator] =
    if (config.get.collectorElevator != null) Some(new CollectorElevator(coreTicks)) else None

  // Collector Extender
  implicit val collectorExtenderHardware = hardware.collectorExtender
  lazy val collectorExtender: Option[CollectorExtender] =
    if (config.get.collectorExtender != null) {
      implicit val gt = () => gearTilter
      implicit val lt = () => loadTray
      Some(new CollectorExtender(coreTicks))
    } else None

  // Collector Rollers
  implicit val collectorRollersHardware = hardware.collectorRollers
  implicit val collectorRollersProps = config.map(_.collectorRollers.properties)
  lazy val collectorRollers: Option[CollectorRollers] =
    if (config.get.collectorRollers != null) Some(new CollectorRollers(coreTicks)) else None

  // Gear Grabber
  implicit val gearGrabberHardware = hardware.gearGrabber
  implicit val gearGrabberProps = config.map(_.gearGrabber.props)
  lazy val gearGrabber: Option[GearGrabber] = {
    implicit val gt = () => gearTilter
    if (config.get.gearGrabber != null) Some(new GearGrabber(coreTicks)) else None
  }

  // Gear Tilter
  implicit val gearTilterHardware = hardware.gearTilter
  implicit lazy val gearTilter: Option[GearTilter] =
    if (config.get.gearTilter != null) {
      implicit val ce = () => collectorExtender
      implicit val gg = () => gearGrabber
      Some(new GearTilter(coreTicks))
    } else None

  // Shooter Flywheel
  implicit val shooterFlywheelHardware = hardware.shooterFlywheel
  implicit val shooterFlywheelProps = config.map(_.shooterFlywheel.props)
  lazy val shooterFlywheel: Option[ShooterFlywheel] =
    if (config.get.shooterFlywheel != null) Some(new ShooterFlywheel(coreTicks)) else None

  // Shooter Shifter
  implicit val shooterShifterHardware = hardware.shooterShifter
  lazy val shooterShifter: Option[ShooterShifter] =
    if (config.get.shooterShifter != null) Some(new ShooterShifter(coreTicks)) else None

  // Load Tray
  implicit val loadTrayHardware = hardware.loadTray
  lazy val loadTray: Option[LoadTray] =
    if (config.get.loadTray != null) {
      implicit val ce = () => collectorExtender
      Some(new LoadTray(coreTicks))
    } else None

  // Lighting
  /**
    * Function to determine what lighting effect should be displayed
    */
  val lightingStatus: () => Int = () => {
    val gearState = gearGrabber.isDefined && gearGrabberHardware.proximitySensor.getVoltage > gearGrabberProps.get.detectingDistance.value
    if (climberPuller.isDefined && climberPullerHardware.motorA.get() > 0) {
      9
    } else if (gearState) {
      5
    } else if (ds.getMatchTime >= 135) {
      16
    } else if (ds.isDisabled) {
      1
    } else if (ds.isAutonomous) {
      if (ds.getAlliance == Alliance.Blue) {
        8
      } else {
        10
      }
    } else if (shooterFlywheel.isDefined && shooterFlywheelHardware.leftMotor.get() != 0) {
      7
    } else {
      0
    }
  }

  val serialPort = Try(new SerialPort(9600, SerialPort.Port.kUSB)).toOption
  val comms: Option[SerialComms] = serialPort.map(new SerialComms(_))
  val lighting: Option[StatusLightingComponent] = comms.map(c => new StatusLightingComponent(lightingStatus, c, coreTicks))

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
    loadTray,
    lighting
  ).flatten

  new ButtonMappings(this)

  val auto = Signal(ds.isEnabled && ds.isAutonomous).filter(identity)

  val generator = new AutoGenerator(this)

  def prepTask(task: Task): Unit = {
    task.init()
    task.abort()
  }

  private var autonomousRoutines = mutable.Map.empty[Int, ContinuousTask]

  def addAutonomousRoutine(id: Int)(task: ContinuousTask): Unit = {
    if (autonomousRoutines.contains(id)) {
      println(s"WARNING, overriding autonomous routine $id")
    }

    autonomousRoutines(id) = task
  }

  for {
    drivetrain <- drivetrain
    gearGrabber <- gearGrabber
    gearTilter <- gearTilter
  } {
    addAutonomousRoutine(1)(
      generator.centerGear(drivetrain, gearGrabber, gearTilter).toContinuous
    )

    addAutonomousRoutine(2)(
      generator.leftGear(drivetrain, gearGrabber, gearTilter).toContinuous
    )

    addAutonomousRoutine(3)(
      generator.rightGear(drivetrain, gearGrabber, gearTilter).toContinuous
    )

    addAutonomousRoutine(4)(
      generator.centerGearAndCrossLine(drivetrain, gearGrabber, gearTilter).toContinuous
    )
  }

  for {
    drivetrain <- drivetrain
    gearGrabber <- gearGrabber
    gearTilter <- gearTilter
    collectorElevator <- collectorElevator
    collectorRollers <- collectorRollers
    agitator <- agitator
    shooterFlywheel <- shooterFlywheel
    collectorExtender <- collectorExtender
    loadTray <- loadTray
  } {
    addAutonomousRoutine(5)(
      generator.shootCenterGear(
        drivetrain,
        gearGrabber, gearTilter,
        collectorElevator, collectorRollers, agitator,
        shooterFlywheel, collectorExtender, loadTray
      ).toContinuous
    )
  }

  for {
    drivetrain <- drivetrain
    collectorElevator <- collectorElevator
    collectorRollers <- collectorRollers
    agitator <- agitator
    shooterFlywheel <- shooterFlywheel
    shooterShifter <- shooterShifter
    collectorExtender <- collectorExtender
    loadTray <- loadTray
  } {
    addAutonomousRoutine(6)(
      generator.leftHopperAndShoot(
        drivetrain,
        collectorElevator, collectorRollers, agitator,
        shooterFlywheel, shooterShifter, collectorExtender, loadTray
      )
    )

    addAutonomousRoutine(7)(
      generator.rightHopperAndShoot(
        drivetrain,
        collectorElevator, collectorRollers, agitator,
        shooterFlywheel, shooterShifter, collectorExtender, loadTray
      )
    )

    addAutonomousRoutine(10)(
      generator.shootLeftAndDriveBack(
        drivetrain,
        collectorElevator, collectorRollers, agitator,
        shooterFlywheel, shooterShifter, collectorExtender, loadTray
      ).toContinuous
    )
  }

  for {
    drivetrain <- drivetrain
  } {
    addAutonomousRoutine(8)(
      generator.slowCrossLine(drivetrain).toContinuous
    )

    addAutonomousRoutine(9)(
      generator.smallTestShot(drivetrain)
    )
  }

  auto.foreach(Signal {
    val autoID = Math.round(SmartDashboard.getNumber("DB/Slider 0")).toInt

    autonomousRoutines.getOrElse(autoID, {
      println(s"ERROR: autonomous routine $autoID not found")
      FiniteTask.empty.toContinuous
    })
  })

  // Needs to go last because component resets have highest priority
  val enabled = Signal(ds.isEnabled).filter(identity)
  enabled.onStart.foreach { () =>
    if (drivetrain.isDefined) {
      drivetrainHardware.gyro.endCalibration()
    }

    components.foreach(_.resetToDefault())
  }

  enabled.onEnd.foreach { () =>
    components.foreach(_.resetToDefault())
  }

  val dashboard = Future {
    val dashboard = new FunkyDashboard(8, 8080)
    dashboard.start()
    dashboard
  }

  dashboard.failed.foreach(_.printStackTrace())

  import CoreRobot._

  dashboard.foreach { board =>
    println("Funky Dashboard is up!")
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      println("Shutting down Funky Dashboard")
      board.stop()
    }))

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
      board.datasetGroup("Drivetrain").addDataset(drivetrainHardware.leftVelocity.map(_.toFeetPerSecond).toTimeSeriesNumeric("Left Ground Velocity"))
      board.datasetGroup("Drivetrain").addDataset(drivetrainHardware.rightVelocity.map(_.toFeetPerSecond).toTimeSeriesNumeric("Right Ground Velocity"))

      board.datasetGroup("Drivetrain").addDataset(drivetrainHardware.leftPosition.map(_.toFeet).toTimeSeriesNumeric("Left Ground"))
      board.datasetGroup("Drivetrain").addDataset(drivetrainHardware.rightPosition.map(_.toFeet).toTimeSeriesNumeric("Right Ground"))

      board.datasetGroup("Drivetrain").addDataset(drivetrainHardware.rootDataStream
        .map(d => (d.leftEncoderRotation * drivetrainProps.get.gearRatio).toDegrees).toTimeSeriesNumeric("Left Wheel Rotation"))

      board.datasetGroup("Drivetrain").addDataset(drivetrainHardware.rootDataStream
        .map(d => (d.rightEncoderRotation * drivetrainProps.get.gearRatio).toDegrees).toTimeSeriesNumeric("Right Wheel Rotation"))

      board.datasetGroup("Drivetrain").addDataset(drivetrainHardware.turnVelocity.map(_.toDegreesPerSecond).toTimeSeriesNumeric("Turn Velocity"))
      board.datasetGroup("Drivetrain").addDataset(drivetrainHardware.turnPosition.map(_.toDegrees).toTimeSeriesNumeric("Rotational Position"))
    }

    shooterFlywheel.foreach { d =>
      board.datasetGroup("Flywheel").addDataset(
        shooterFlywheelHardware.leftVelocity.map(_.toRevolutionsPerMinute).toTimeSeriesNumeric("Left Speed"))

      board.datasetGroup("Flywheel").addDataset(
        shooterFlywheelHardware.rightVelocity.map(_.toRevolutionsPerMinute).toTimeSeriesNumeric("Right Speed"))

      board.datasetGroup("Flywheel").addDataset(new TimeSeriesNumeric("Left Out")(
        shooterFlywheelHardware.leftMotor.get()
      ))

      board.datasetGroup("Flywheel").addDataset(new TimeSeriesNumeric("Right Out")(
        shooterFlywheelHardware.rightMotor.get()
      ))

      board.datasetGroup("Flywheel").addDataset(
        shooterFlywheelHardware.rightVelocity.zip(shooterFlywheelHardware.leftVelocity)
          .map(t => (t._1 - t._2).toRevolutionsPerMinute).toTimeSeriesNumeric("Right - Left"))
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

object CoreRobot {
  implicit class ToTimeSeriesNumeric[T](val stream: Stream[T]) extends AnyVal {
    def toTimeSeriesNumeric(name: String)(implicit ev: T => Double): TimeSeriesNumeric = {
      var lastValue: Double = 0.0
      new TimeSeriesNumeric(name)(lastValue) {
        val cancel = stream.foreach { v =>
          lastValue = v
        }
      }
    }
  }
}
