package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.funkydashboard.{FunkyDashboard, JsonEditor, TimeSeriesNumeric}
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.events.ImpulseEvent
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.{ContinuousTask, FiniteTask, Task}
import com.lynbrookrobotics.potassium.{Component, Signal}
import com.lynbrookrobotics.seventeen.agitator.Agitator
import com.lynbrookrobotics.seventeen.camselect.CamSelect
import com.lynbrookrobotics.seventeen.climber.puller.ClimberPuller
import com.lynbrookrobotics.seventeen.collector.elevator.CollectorElevator
import com.lynbrookrobotics.seventeen.collector.extender.CollectorExtender
import com.lynbrookrobotics.seventeen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.seventeen.drivetrain._
import com.lynbrookrobotics.seventeen.gear.roller.GearRoller
import com.lynbrookrobotics.seventeen.gear.tilter.GearTilter
import com.lynbrookrobotics.seventeen.lighting.{SerialComms, StatusLightingComponent}
import com.lynbrookrobotics.seventeen.loadtray.LoadTray
import com.lynbrookrobotics.seventeen.shooter.flywheel.ShooterFlywheel
import com.lynbrookrobotics.seventeen.shooter.shifter.ShooterShifter
import edu.wpi.first.wpilibj.DriverStation.Alliance
import edu.wpi.first.wpilibj._
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

class CoreRobot(configFileValue: Signal[String], updateConfigFile: String => Unit, val coreTicks: Stream[Unit])
               (implicit val config: Signal[RobotConfig], hardware: RobotHardware,
                val clock: Clock, val polling: ImpulseEvent) {
  implicit val driverHardware = hardware.driver
  private val ds = driverHardware.station

  // Drivetrain
  implicit val drivetrainHardware = hardware.drivetrain
  implicit val drivetrainProps = config.map(_.drivetrain.properties)
  val drivetrain: Option[Drivetrain] =
    if (config.get.drivetrain != null) Some(new Drivetrain) else None

  // Agitator
  implicit val agitatorHardware = hardware.agitator
  implicit val agitatorProps = config.map(_.agitator.properties)
  val agitator: Option[Agitator] =
    if (config.get.agitator != null) Some(new Agitator(coreTicks)) else None

  // CamSelect
  implicit val camSelectHardware = hardware.camSelect
  implicit val camselectProps = config.map(_.camSelect.properties)
  implicit val camSelect: CamSelect = new CamSelect(coreTicks)

  // Climber Puller
  implicit val climberPullerHardware = hardware.climberPuller
  implicit val climberPullerProps = config.map(_.climberPuller.props)
  val climberPuller: Option[ClimberPuller] =
    if (config.get.climberPuller != null) Some(new ClimberPuller(coreTicks)) else None

  // Collector Elevator
  implicit val collectorElevatorHardware = hardware.collectorElevator
  implicit val collectorElevatorProps = config.map(_.collectorElevator.properties)
  val collectorElevator: Option[CollectorElevator] =
    if (config.get.collectorElevator != null) Some(new CollectorElevator(coreTicks)) else None

  // Collector Extender
  implicit val collectorExtenderHardware = hardware.collectorExtender
  val collectorExtender: Option[CollectorExtender] =
    if (config.get.collectorExtender != null) {
      Some(new CollectorExtender(coreTicks, gearTilter))
    } else None

  // Collector Rollers
  implicit val collectorRollersHardware = hardware.collectorRollers
  implicit val collectorRollersProps = config.map(_.collectorRollers.properties)
  val collectorRollers: Option[CollectorRollers] =
    if (config.get.collectorRollers != null) Some(new CollectorRollers(coreTicks)) else None

  // Gear Grabber
  implicit val gearRollerHardware = hardware.gearRoller
  implicit val gearGrabberProps = config.map(_.gearRoller.props)
  val gearRoller: Option[GearRoller] = {
    if (config.get.gearRoller != null) Some(new GearRoller(coreTicks)) else None
  }

  println(gearRoller)

  // Gear Tilter
  implicit val gearTilterHardware = hardware.gearTilter
  val gearTilter: Option[GearTilter] =
    if (config.get.gearTilter != null) {
      Some(new GearTilter(coreTicks, collectorExtender))
    } else None

  println(gearTilter)

  // Shooter Flywheel
  implicit val shooterFlywheelHardware = hardware.shooterFlywheel
  implicit val shooterFlywheelProps = config.map(_.shooterFlywheel.props)
  val shooterFlywheel: Option[ShooterFlywheel] =
    if (config.get.shooterFlywheel != null) Some(new ShooterFlywheel(coreTicks)) else None

  // Shooter Shifter
  implicit val shooterShifterHardware = hardware.shooterShifter
  val shooterShifter: Option[ShooterShifter] =
    if (config.get.shooterShifter != null) Some(new ShooterShifter(coreTicks)) else None

  // Load Tray
  implicit val loadTrayHardware = hardware.loadTray
  val loadTray: Option[LoadTray] =
    if (config.get.loadTray != null) {
      Some(new LoadTray(coreTicks))
    } else None

  // Lighting
  /**
    * Function to determine what lighting effect should be displayed
    */
  val lightingStatus: () => Int = () => {
    val gearState = gearRoller.isDefined && false
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

  val comms: Option[SerialComms] = Try(new SerialPort(9600, SerialPort.Port.kUSB)).map(new SerialComms(_)).toOption
  val lighting: Option[StatusLightingComponent] = comms.map(c => new StatusLightingComponent(lightingStatus, c, coreTicks))

  new Compressor().start()

  private val components: List[Component[_]] = List(
    drivetrain,
    agitator,
    climberPuller,
    collectorElevator,
    collectorExtender,
    collectorRollers,
    gearRoller,
    gearTilter,
    shooterFlywheel,
    shooterShifter,
    loadTray,
    lighting
  ).flatten

  private val mappings = new ButtonMappings(this)

  private val inAutonomous = Signal(ds.isEnabled && ds.isAutonomous).filter(identity)

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

    prepTask(task)

    autonomousRoutines(id) = task
  }

  for {
    drivetrain <- drivetrain
    gearRoller <- gearRoller
    gearTilter <- gearTilter
  } {
    addAutonomousRoutine(1)(
      generator.centerGear(drivetrain, gearRoller, gearTilter).toContinuous
    )

    addAutonomousRoutine(2)(
      generator.leftGear(drivetrain, gearRoller, gearTilter).toContinuous
    )

    addAutonomousRoutine(3)(
      generator.rightGear(drivetrain, gearRoller, gearTilter).toContinuous
    )

    addAutonomousRoutine(4)(
      generator.centerGearAndCrossLine(drivetrain, gearRoller, gearTilter).toContinuous
    )

    addAutonomousRoutine(11) {
      generator.leftGearPurePursuit(drivetrain, gearRoller, gearTilter).toContinuous
    }

    addAutonomousRoutine(12) {
      generator.rightGearPurePursuit(drivetrain, gearRoller, gearTilter).toContinuous
    }
  }

  for {
    drivetrain <- drivetrain
    gearGrabber <- gearRoller
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

  inAutonomous.foreach(Signal {
    val autoID = Math.round(SmartDashboard.getNumber("DB/Slider 0")).toInt

    autonomousRoutines.getOrElse(autoID, {
      println(s"ERROR: autonomous routine $autoID not found")
      FiniteTask.empty.toContinuous
    })
  })

  // Needs to go last because component resets have highest priority
  private val enabled = Signal(ds.isEnabled).filter(identity)
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
    val dashboard = new FunkyDashboard(10, 8080)
    dashboard.start()
    dashboard
  }

  dashboard.failed.foreach(_.printStackTrace())

  dashboard.foreach { board =>
    import CoreRobot.ToTimeSeriesNumeric

    println("Funky Dashboard is up!")

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
      board.datasetGroup("Drivetrain/Velocity").addDataset(drivetrainHardware.leftVelocity.map(_.toFeetPerSecond).toTimeSeriesNumeric("Left Ground Velocity"))
      board.datasetGroup("Drivetrain/Velocity").addDataset(drivetrainHardware.rightVelocity.map(_.toFeetPerSecond).toTimeSeriesNumeric("Right Ground Velocity"))

      board.datasetGroup("Drivetrain/Velocity").addDataset(new TimeSeriesNumeric("Left Encoder Ticks/s")(drivetrainHardware.leftBack.getSpeed * 10))
      board.datasetGroup("Drivetrain/Velocity").addDataset(new TimeSeriesNumeric("Right Encoder Ticks/s")(drivetrainHardware.rightBack.getSpeed * 10))
      board.datasetGroup("Drivetrain/Velocity").addDataset(new TimeSeriesNumeric("Left Out")(drivetrainHardware.leftBack.get()))
      board.datasetGroup("Drivetrain/Velocity").addDataset(new TimeSeriesNumeric("Right Out")(drivetrainHardware.rightBack.get()))

      board.datasetGroup("Drivetrain/Position").addDataset(drivetrainHardware.leftPosition.map(_.toFeet).toTimeSeriesNumeric("Left Ground"))
      board.datasetGroup("Drivetrain/Position").addDataset(drivetrainHardware.rightPosition.map(_.toFeet).toTimeSeriesNumeric("Right Ground"))

      board.datasetGroup("Drivetrain/Position").addDataset(drivetrainHardware.rootDataStream
        .map(d => (d.leftEncoderRotation * drivetrainProps.get.gearRatio).toDegrees).toTimeSeriesNumeric("Left Wheel Rotation"))

      board.datasetGroup("Drivetrain/Position").addDataset(drivetrainHardware.rootDataStream
        .map(d => (d.rightEncoderRotation * drivetrainProps.get.gearRatio).toDegrees).toTimeSeriesNumeric("Right Wheel Rotation"))

      board.datasetGroup("Drivetrain/Gyro").addDataset(drivetrainHardware.turnVelocity.map(_.toDegreesPerSecond).toTimeSeriesNumeric("Turn Velocity"))
      board.datasetGroup("Drivetrain/Gyro").addDataset(drivetrainHardware.turnPosition.map(_.toDegrees).toTimeSeriesNumeric("Rotational Position"))
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

    gearRoller.foreach { g =>
      board.datasetGroup("Grabber").addDataset(new TimeSeriesNumeric("Motor Current")(
        hardware.gearRoller.motor.getOutputCurrent
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
