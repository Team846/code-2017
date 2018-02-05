package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.funkydashboard.{FunkyDashboard, JsonEditor, TimeSeriesNumeric}
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.events.ImpulseEvent
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.{ContinuousTask, FiniteTask, Task}
import com.lynbrookrobotics.potassium.{Component, Signal}
import com.lynbrookrobotics.seventeen.agitator.{Agitator, AgitatorHardware, AgitatorProperties}
import com.lynbrookrobotics.seventeen.camselect.{CamSelect, CamSelectHardware, CamSelectProperties}
import com.lynbrookrobotics.seventeen.climber.puller.{ClimberPuller, ClimberPullerHardware, ClimberPullerProperties}
import com.lynbrookrobotics.seventeen.collector.elevator.{CollectorElevator, CollectorElevatorHardware}
import com.lynbrookrobotics.seventeen.collector.extender.CollectorExtender
import com.lynbrookrobotics.seventeen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.seventeen.driver.DriverHardware
import com.lynbrookrobotics.seventeen.drivetrain._
import com.lynbrookrobotics.seventeen.gear.grabber.GearGrabber
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

class CoreRobot(configFileValue: Signal[String],
                updateConfigFile: String => Unit,
                val coreTicks: Stream[Unit])
               (implicit val config: Signal[RobotConfig], hardware: RobotHardware,
                val clock: Clock, val polling: ImpulseEvent) {
  println("starting core robot")
  implicit val driverHardware: DriverHardware = hardware.driver
  println("line 36")
  private val ds = driverHardware.station

  println("line 39")
  // Drivetrain
  implicit val drivetrainHardware: DrivetrainHardware = hardware.drivetrain
  implicit val drivetrainProps: Signal[DrivetrainProperties] = config.map(_.drivetrain.properties)
  val drivetrain: Option[Drivetrain] =
    if (config.get.drivetrain != null) Some(new Drivetrain) else None

  println("line 46")
  // Agitator
  implicit val agitatorHardware: AgitatorHardware = hardware.agitator
  implicit val agitatorProps: Signal[AgitatorProperties] = config.map(_.agitator.properties)
  val agitator: Option[Agitator] =
    if (config.get.agitator != null) Some(new Agitator(coreTicks)) else None

  println("line 53")
  // CamSelect
  implicit val camSelectHardware: CamSelectHardware = hardware.camSelect
  implicit val camselectProps: Signal[CamSelectProperties] = config.map(_.camSelect.properties)
  implicit val camSelect: CamSelect = new CamSelect(coreTicks)

  println("line 59")
  // Climber Puller
  implicit val climberPullerHardware: ClimberPullerHardware = hardware.climberPuller
  implicit val climberPullerProps: Signal[ClimberPullerProperties] = config.map(_.climberPuller.props)
  val climberPuller: Option[ClimberPuller] =
    if (config.get.climberPuller != null) Some(new ClimberPuller(coreTicks)) else None

  println("line 66")
  // Collector Elevator
  implicit val collectorElevatorHardware: CollectorElevatorHardware = hardware.collectorElevator
  implicit val collectorElevatorProps = config.map(_.collectorElevator.properties)
  val collectorElevator: Option[CollectorElevator] =
  /*if (config.get.collectorElevator != null) Some(new CollectorElevator(coreTicks)) else */ None

  println("collector Extender")
  // Collector Extender
  implicit val collectorExtenderHardware = hardware.collectorExtender
  val collectorExtender: Option[CollectorExtender] =
  /*if (config.get.collectorExtender != null) {
    Some(new CollectorExtender(coreTicks, gearTilter))
  } else */ None

  println("collector rollers")
  // Collector Rollers
  implicit val collectorRollersHardware = hardware.collectorRollers
  implicit val collectorRollersProps = config.map(_.collectorRollers.properties)
  val collectorRollers: Option[CollectorRollers] =
  /*if (config.get.collectorRollers != null) Some(new CollectorRollers(coreTicks)) else*/ None

  println("gear grabber hardware")
  // Gear Grabber
  implicit val gearGrabberHardware = hardware.gearGrabber
  implicit val gearGrabberProps = config.map(_.gearGrabber.props)
  val gearGrabber: Option[GearGrabber] = {
    implicit val gt = () => gearTilter
    /*if (config.get.gearGrabber != null) Some(new GearGrabber(coreTicks)) else */ None
  }

  println("tilter")
  // Gear Tilter
  implicit val gearTilterHardware = hardware.gearTilter
  val gearTilter: Option[GearTilter] =
  /*if (config.get.gearTilter != null) {
    Some(new GearTilter(coreTicks, gearGrabber, collectorExtender))
  } else*/ None

  println("flywheel")
  // Shooter Flywheel
  implicit val shooterFlywheelHardware = hardware.shooterFlywheel
  implicit val shooterFlywheelProps = config.map(_.shooterFlywheel.props)
  val shooterFlywheel: Option[ShooterFlywheel] =
  /*if (config.get.shooterFlywheel != null) Some(new ShooterFlywheel(coreTicks)) else */ None

  // Shooter Shifter
  implicit val shooterShifterHardware = hardware.shooterShifter
  val shooterShifter: Option[ShooterShifter] =
  /*if (config.get.shooterShifter != null) Some(new ShooterShifter(coreTicks)) else */ None

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
    val gearState = gearGrabber.isDefined && gearGrabberHardware.proximitySensor.getVoltage > gearGrabberProps.get.detectingDistance.value
    if (climberPuller.isDefined && climberPullerHardware.motorA.getMotorOutputPercent > 0) {
      9
    }
    else if (gearState) {
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
    gearGrabber,
    gearTilter,
    shooterFlywheel,
    shooterShifter,
    loadTray,
    lighting
  ).flatten

  private val mappings = new ButtonMappings(this)



  val generator = new AutoGenerator(this)

  def prepTask(task: Task): Unit = {
    try {
      task.init()
      task.abort()
    } catch {
      case e => {
        e.printStackTrace()
        println("failed")
      }
    }

  }

  private var autonomousRoutines = mutable.Map.empty[Int, ContinuousTask]

  println("add auto")

  def addAutonomousRoutine(id: Int)(task: ContinuousTask): Unit = {
    if (autonomousRoutines.contains(id)) {
      println(s"WARNING, overriding autonomous routine $id")
    }
    println("Prepping auto task")

    prepTask(task)

    println("finished prepping auto")

    autonomousRoutines(id) = task
  }

  println("before adding drivetrain")
  //  for {
  //    drivetrain <- drivetrain
  //  } {
  //    println("before routine 1")
  //    addAutonomousRoutine(1)(
  //      generator.leftGearPurePursuitNoGear(drivetrain).toContinuous
  //    )
  //    println("after adding routine 1")
  //  }
  //  for {
  //    drivetrain <- drivetrain
  //    gearGrabber <- gearGrabber
  //    gearTilter <- gearTilter
  //  } {
  //    addAutonomousRoutine(1)(
  //      generator.centerGear(drivetrain, gearGrabber, gearTilter).toContinuous
  //    )
  //
  //    addAutonomousRoutine(2)(
  //      generator.leftGear(drivetrain, gearGrabber, gearTilter).toContinuous
  //    )
  //
  //    addAutonomousRoutine(3)(
  //      generator.rightGear(drivetrain, gearGrabber, gearTilter).toContinuous
  //    )
  //
  //    addAutonomousRoutine(4)(
  //      generator.centerGearAndCrossLine(drivetrain, gearGrabber, gearTilter).toContinuous
  //    )
  //
  //    addAutonomousRoutine(11) {
  //      generator.leftGearPurePursuit(drivetrain, gearGrabber, gearTilter).toContinuous
  //    }
  //
  //    addAutonomousRoutine(12) {
  //      generator.rightGearPurePursuit(drivetrain, gearGrabber, gearTilter).toContinuous
  //    }
  //  }

  //  for {
  //    drivetrain <- drivetrain
  //    gearGrabber <- gearGrabber
  //    gearTilter <- gearTilter
  //    collectorElevator <- collectorElevator
  //    collectorRollers <- collectorRollers
  //    agitator <- agitator
  //    shooterFlywheel <- shooterFlywheel
  //    collectorExtender <- collectorExtender
  //    loadTray <- loadTray
  //  } {
  //    addAutonomousRoutine(5)(
  //      generator.shootCenterGear(
  //        drivetrain,
  //        gearGrabber, gearTilter,
  //        collectorElevator, collectorRollers, agitator,
  //        shooterFlywheel, collectorExtender, loadTray
  //      ).toContinuous
  //    )
  //  }

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
      addAutonomousRoutine(6) {
        println("add auto 6")
        generator.leftHopperAndShoot(
          drivetrain,
          collectorElevator, collectorRollers, agitator,
          shooterFlywheel, shooterShifter, collectorExtender, loadTray
        )
      }

      addAutonomousRoutine(7) {
        println("add auto 7")
        generator.rightHopperAndShoot(
          drivetrain,
          collectorElevator, collectorRollers, agitator,
          shooterFlywheel, shooterShifter, collectorExtender, loadTray
        )
      }

      addAutonomousRoutine(10) {
        println("adding auto 10")
        generator.shootLeftAndDriveBack(
          drivetrain,
          collectorElevator, collectorRollers, agitator,
          shooterFlywheel, shooterShifter, collectorExtender, loadTray
        ).toContinuous
      }
    }

    for {
      drivetrain <- drivetrain
    } {
      println("adding auto 1")
      addAutonomousRoutine(1) {
        generator.centerDriveBack(drivetrain).toContinuous
      }

      println("adding auto 2")
      addAutonomousRoutine(2){
        generator.driveForwardOpenLoop(drivetrain, coreTicks)
      }

      println("adding auto 3")
      addAutonomousRoutine(3){
        generator.driveForwardOpenLoop5seconds(drivetrain, coreTicks)
      }

      println("adding auto 8")
      addAutonomousRoutine(8)(
        generator.slowCrossLine(drivetrain).toContinuous
      )

      println("auto 9")

      addAutonomousRoutine(9)(
        generator.smallTestShot(drivetrain)
      )
    }

  import driverHardware._

  isAutonomousEnabled.foreach(Signal {
    val autoID = Math.round(SmartDashboard.getNumber("DB/Slider 0", 0)).toInt

    autonomousRoutines.getOrElse(autoID, {
      println(s"ERROR: autonomous routine $autoID not found")
      FiniteTask.empty.toContinuous
    })
  })

  // Needs to go last because component resets have highest priority
  isEnabled.onStart.foreach { () =>
    if (drivetrain.isDefined) {
      drivetrainHardware.gyro.endCalibration()
    }
  }

  isTelopEnabled.onStart.foreach { () =>
    println("telop enabled starting")
    components.foreach(_.resetToDefault())
  }

  isEnabled.onEnd.foreach { () =>
    components.foreach(_.resetToDefault())
  }

//  val dashboard = Future {
//    val dashboard = new FunkyDashboard(100, 8080)
//    dashboard.start()
//    dashboard
//  }
//
//  dashboard.failed.foreach(_.printStackTrace())
//
//  dashboard.foreach { board =>
//    import CoreRobot.ToTimeSeriesNumeric
//
//    println("Funky Dashboard is up!")
//    Runtime.getRuntime.addShutdownHook(new Thread(() => {
//      println("Shutting down Funky Dashboard")
//      board.stop()
//    }))
//
//    board.datasetGroup("Config").addDataset(new JsonEditor("Robot Config")(
//      configFileValue.get,
//      updateConfigFile
//    ))
//
//    board.datasetGroup("Power").addDataset(new TimeSeriesNumeric("Battery Voltage")(
//      ds.getBatteryVoltage
//    ))
//
//    board.datasetGroup("Joysticks").addDataset(new TimeSeriesNumeric("POV")(
//      driverHardware.operatorJoystick.getPOV()
//    ))
//
//    drivetrain.foreach { d =>
//      board.datasetGroup("Drivetrain/Velocity").addDataset(drivetrainHardware.leftVelocity.map(_.toFeetPerSecond).toTimeSeriesNumeric("Left Ground Velocity"))
//      board.datasetGroup("Drivetrain/Velocity").addDataset(drivetrainHardware.rightVelocity.map(_.toFeetPerSecond).toTimeSeriesNumeric("Right Ground Velocity"))
//
//      board.datasetGroup("Drivetrain/Velocity").addDataset(new TimeSeriesNumeric("Left Encoder Ticks/s")(drivetrainHardware.leftBack.t.getSensorCollection.getQuadratureVelocity * 10))
//      board.datasetGroup("Drivetrain/Velocity").addDataset(new TimeSeriesNumeric("Right Encoder Ticks/s")(drivetrainHardware.rightBack.t.getSensorCollection.getQuadratureVelocity * 10))
//      board.datasetGroup("Drivetrain/Velocity").addDataset(new TimeSeriesNumeric("Left Out")(drivetrainHardware.leftBack.t.getMotorOutputPercent))
//      board.datasetGroup("Drivetrain/Velocity").addDataset(new TimeSeriesNumeric("Right Out")(drivetrainHardware.rightBack.t.getMotorOutputPercent))
//
//      board.datasetGroup("Drivetrain/Position").addDataset(drivetrainHardware.leftPosition.map(_.toFeet).toTimeSeriesNumeric("Left Ground"))
//      board.datasetGroup("Drivetrain/Position").addDataset(drivetrainHardware.rightPosition.map(_.toFeet).toTimeSeriesNumeric("Right Ground"))
//
//      board.datasetGroup("Drivetrain/Position").addDataset(drivetrainHardware.rootDataStream
//        .map(d => (d.leftEncoderRotation * drivetrainProps.get.gearRatio).toDegrees).toTimeSeriesNumeric("Left Wheel Rotation"))
//
//      board.datasetGroup("Drivetrain/Position").addDataset(drivetrainHardware.rootDataStream
//        .map(d => (d.rightEncoderRotation * drivetrainProps.get.gearRatio).toDegrees).toTimeSeriesNumeric("Right Wheel Rotation"))
//
//      board.datasetGroup("Drivetrain/Gyro").addDataset(drivetrainHardware.turnVelocity.map(_.toDegreesPerSecond).toTimeSeriesNumeric("Turn Velocity"))
//      board.datasetGroup("Drivetrain/Gyro").addDataset(drivetrainHardware.turnPosition.map(_.toDegrees).toTimeSeriesNumeric("Rotational Position"))
//    }
//
//    shooterFlywheel.foreach { d =>
//      board.datasetGroup("Flywheel").addDataset(
//        shooterFlywheelHardware.leftVelocity.map(_.toRevolutionsPerMinute).toTimeSeriesNumeric("Left Speed"))
//
//      board.datasetGroup("Flywheel").addDataset(
//        shooterFlywheelHardware.rightVelocity.map(_.toRevolutionsPerMinute).toTimeSeriesNumeric("Right Speed"))
//
//      board.datasetGroup("Flywheel").addDataset(new TimeSeriesNumeric("Left Out")(
//        shooterFlywheelHardware.leftMotor.get()
//      ))
//
//      board.datasetGroup("Flywheel").addDataset(new TimeSeriesNumeric("Right Out")(
//        shooterFlywheelHardware.rightMotor.get()
//      ))
//
//      board.datasetGroup("Flywheel").addDataset(
//        shooterFlywheelHardware.rightVelocity.zip(shooterFlywheelHardware.leftVelocity)
//          .map(t => (t._1 - t._2).toRevolutionsPerMinute).toTimeSeriesNumeric("Right - Left"))
//    }
//
//    climberPuller.foreach { c =>
//      board.datasetGroup("Climber").addDataset(new TimeSeriesNumeric("Motor A")(
//        hardware.pdp.getCurrent(3)
//      ))
//
//      board.datasetGroup("Climber").addDataset(new TimeSeriesNumeric("Motor B")(
//        hardware.pdp.getCurrent(2)
//      ))
//    }
//
//    gearGrabber.foreach { g =>
//      board.datasetGroup("Grabber").addDataset(new TimeSeriesNumeric("IR Distance")(
//        hardware.gearGrabber.proximitySensor.getVoltage
//      ))
//    }
//  }
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
