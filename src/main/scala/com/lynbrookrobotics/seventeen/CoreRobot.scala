package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.funkydashboard.{FunkyDashboard, JsonEditor, TimeSeriesNumeric}
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.events.ImpulseEvent
import com.lynbrookrobotics.potassium.tasks.{FiniteTask, Task}
import com.lynbrookrobotics.potassium.{Component, Signal}
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
import edu.wpi.first.wpilibj.DriverStation.Alliance
import edu.wpi.first.wpilibj._
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try
import com.lynbrookrobotics.potassium.streams.Stream

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
  lazy val gearGrabber: Option[GearGrabber] =
    if (config.get.gearGrabber != null) Some(new GearGrabber(coreTicks)) else None

  // Gear Tilter
  implicit val gearTilterHardware = hardware.gearTilter
  lazy val gearTilter: Option[GearTilter] =
    if (config.get.gearTilter != null) {
      implicit val ce = () => collectorExtender
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
  val comms: Option[SerialComms] = None// serialPort.map(new SerialComms(_))
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

  drivetrain.foreach { implicit dr =>
    gearGrabber.foreach { implicit gg =>
      gearTilter.foreach { implicit t =>
        prepTask(generator.centerGear)
        prepTask(generator.leftGear)
        prepTask(generator.rightGear)
        prepTask(generator.centerGearAndCrossLine)
      }
    }
  }

  drivetrain.foreach { implicit dr =>
    gearGrabber.foreach { implicit gg =>
      collectorElevator.foreach { implicit ce =>
        collectorRollers.foreach { implicit cr =>
          agitator.foreach { implicit a =>
            shooterFlywheel.foreach { implicit f =>
              gearTilter.foreach { implicit t =>
                collectorExtender.foreach { implicit ex =>
                  shooterShifter.foreach { implicit sh =>
                    loadTray.foreach { implicit lt =>
                      prepTask(generator.shootCenterGear)
                      prepTask(generator.leftHopperAndShoot)
                      prepTask(generator.rightHopperAndShoot)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  drivetrain.foreach { implicit dr =>
    prepTask(generator.slowCrossLine)
  }

  auto.foreach(Signal {
    val autoID = Math.round(SmartDashboard.getNumber("DB/Slider 0"))

    if (autoID == 1) {
      drivetrain.flatMap { implicit dr =>
        gearGrabber.flatMap { implicit gg =>
          gearTilter.map { implicit t =>
            generator.centerGear.toContinuous
          }
        }
      }.getOrElse(FiniteTask.empty.toContinuous)
    } else if (autoID == 2) {
      drivetrain.flatMap { implicit dr =>
        gearGrabber.flatMap { implicit gg =>
          gearTilter.map { implicit t =>
            generator.leftGear.toContinuous
          }
        }
      }.getOrElse(FiniteTask.empty.toContinuous)
    } else if (autoID == 3) {
      drivetrain.flatMap { implicit dr =>
        gearGrabber.flatMap { implicit gg =>
          gearTilter.map { implicit t =>
            generator.rightGear.toContinuous
          }
        }
      }.getOrElse(FiniteTask.empty.toContinuous)
    } else if (autoID == 4) {
      drivetrain.flatMap { implicit dr =>
        gearGrabber.flatMap { implicit gg =>
          gearTilter.map { implicit t =>
            generator.centerGearAndCrossLine.toContinuous
          }
        }
      }.getOrElse(FiniteTask.empty.toContinuous)
    } else if (autoID == 5) {
      drivetrain.flatMap { implicit dr =>
        gearGrabber.flatMap { implicit gg =>
          collectorElevator.flatMap { implicit ce =>
            collectorRollers.flatMap { implicit cr =>
              agitator.flatMap { implicit a =>
                shooterFlywheel.flatMap { implicit f =>
                  gearTilter.flatMap { implicit t =>
                    collectorExtender.flatMap { implicit ex =>
                      loadTray.map { implicit lt =>
                        generator.shootCenterGear.toContinuous
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }.getOrElse(FiniteTask.empty.toContinuous)
    } else if (autoID == 6) {
      drivetrain.flatMap { implicit dr =>
        gearGrabber.flatMap { implicit gg =>
          collectorElevator.flatMap { implicit ce =>
            collectorRollers.flatMap { implicit cr =>
              agitator.flatMap { implicit a =>
                shooterFlywheel.flatMap { implicit f =>
                  gearTilter.flatMap { implicit t =>
                    collectorExtender.flatMap { implicit ex =>
                      shooterShifter.flatMap { implicit sh =>
                        loadTray.map { implicit lt =>
                          generator.leftHopperAndShoot
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }.getOrElse(FiniteTask.empty.toContinuous)
    } else if (autoID == 7) {
      drivetrain.flatMap { implicit dr =>
        gearGrabber.flatMap { implicit gg =>
          collectorElevator.flatMap { implicit ce =>
            collectorRollers.flatMap { implicit cr =>
              agitator.flatMap { implicit a =>
                shooterFlywheel.flatMap { implicit f =>
                  gearTilter.flatMap { implicit t =>
                    collectorExtender.flatMap { implicit ex =>
                      shooterShifter.flatMap { implicit sh =>
                        loadTray.map { implicit lt =>
                          generator.rightHopperAndShoot
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }.getOrElse(FiniteTask.empty.toContinuous)
    } else if (autoID == 8) {
      drivetrain.map { implicit dr =>
        generator.slowCrossLine.toContinuous
      }.getOrElse(FiniteTask.empty.toContinuous)
    } else if (autoID == 9) {
      drivetrain.flatMap { implicit dr =>
        gearGrabber.flatMap { implicit gg =>
          collectorElevator.flatMap { implicit ce =>
            collectorRollers.flatMap { implicit cr =>
              agitator.flatMap { implicit a =>
                shooterFlywheel.flatMap { implicit f =>
                  gearTilter.flatMap { implicit t =>
                    collectorExtender.flatMap { implicit ex =>
                      shooterShifter.flatMap { implicit sh =>
                        loadTray.map { implicit lt =>
                          generator.smallTestShot
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }.getOrElse(FiniteTask.empty.toContinuous)
      } else {
      FiniteTask.empty.toContinuous
    }
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

  val dashboard = Try {
    val dashboard = new FunkyDashboard(125, 8080)
    dashboard.start()
    dashboard
  }

  dashboard.failed.foreach(_.printStackTrace())

  import CoreRobot._

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