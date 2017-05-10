package com.lynbrookrobotics.seventeen

import javafx.scene.input.RotateEvent

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.frc.Implicits._
import com.lynbrookrobotics.potassium.tasks.{ContinuousTask, Task}
import com.lynbrookrobotics.seventeen.agitator.SpinAgitator
import com.lynbrookrobotics.seventeen.camselect._
import com.lynbrookrobotics.seventeen.climber.ClimberTasks
import com.lynbrookrobotics.seventeen.collector.CollectorTasks
import com.lynbrookrobotics.seventeen.collector.extender.CollectorExtenderExtended
import com.lynbrookrobotics.seventeen.gear.GearTasks
import com.lynbrookrobotics.seventeen.gear.grabber.OpenGrabber
import com.lynbrookrobotics.seventeen.loadtray.ExtendTray
import com.lynbrookrobotics.seventeen.shooter.ShooterTasks
import com.lynbrookrobotics.seventeen.shooter.flywheel.velocityTasks.{WhileAtDoubleVelocity, WhileAtVelocity}
import com.lynbrookrobotics.seventeen.shooter.shifter.{ShooterShiftLeft, ShooterShiftRight}
import edu.wpi.first.wpilibj.Utility
import squants.Percent
import squants.time.{Frequency, Microseconds, Milliseconds, RevolutionsPerMinute}
import com.lynbrookrobotics.seventeen.gear.tilter.ExtendTilter
import com.lynbrookrobotics.seventeen.shooter.flywheel.ShooterFlywheelProperties
import com.lynbrookrobotics.seventeen.shooter.shifter.{ShiftShooter, ShooterShiftLeft, ShooterShiftRight}
import squants.time.{Frequency, RevolutionsPerMinute}

class ButtonMappings(r: CoreRobot) {

  import r._

  var curFlywheelSpeedLeft: Frequency = if (config.get.shooterFlywheel != null) {
    config.get.shooterFlywheel.props.midShootSpeedLeft
  } else RevolutionsPerMinute(0)

  var curFlywheelSpeedRight: Frequency = if (config.get.shooterFlywheel != null) {
    config.get.shooterFlywheel.props.midShootSpeedRight
  } else RevolutionsPerMinute(0)

  val flywheelSpeedLeft = Signal(curFlywheelSpeedLeft)
  val flywheelSpeedRight = Signal(curFlywheelSpeedRight)

  shooterFlywheel.zip(collectorElevator).zip(collectorRollers).zip(shooterShifter).zip(agitator).zip(collectorExtender).zip(loadTray).foreach { t =>
    implicit val ((((((fly, elev), roll), shift), agitator), ex), lt) = t

    val time = Signal {
      Microseconds(Utility.getFPGATime)
    }

    /**
      * Shoots fuel at high speed
      * Trigger pressed
      */
    val shootFuelPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.Trigger)
    shootFuelPressed.foreach(ShooterTasks.continuousShoot(flywheelSpeedLeft, flywheelSpeedRight))

    /**
      * Shifts shooter to left
      * TriggerLeft pressed
      */
    val shiftShooterLeftPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.TriggerLeft)
    shiftShooterLeftPressed.foreach(() => shift.currentState = ShooterShiftLeft)

    /**
      * Shifts shooter to right
      * TriggerRight pressed
      */
    val shiftShooterRightPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.TriggerRight)
    shiftShooterRightPressed.foreach(() => shift.currentState = ShooterShiftRight)

    driverHardware.operatorJoystick.buttonPressed(JoystickButtons.RightTwo)
      .foreach(new ContinuousTask {
        override protected def onStart(): Unit = {
          agitator.setController(Signal.constant(Percent(50)).toPeriodic)
          elev.setController(Signal.constant(-Percent(50)).toPeriodic)
          roll.setController(Signal.constant(-Percent(50)).toPeriodic)
          ex.setController(Signal.constant(CollectorExtenderExtended).toPeriodic)
        }

        override protected def onEnd(): Unit = {
          agitator.resetToDefault()
          elev.resetToDefault()
          roll.resetToDefault()
          ex.resetToDefault()
        }


      })
  }

  shooterFlywheel.foreach { implicit fly =>
    val time = Signal {
      Milliseconds(System.currentTimeMillis())
    }

    /**
      * Flywheel speed set to low speed
      * LeftOne pressed
      */
    val setLowFlywheelSpeed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftOne)
    setLowFlywheelSpeed.foreach(() => {
      curFlywheelSpeedLeft = config.get.shooterFlywheel.props.lowShootSpeedLeft
      curFlywheelSpeedRight = config.get.shooterFlywheel.props.lowShootSpeedRight
    })

    /**
      * Flywheel speed set to medium speed
      * LeftTwo pressed
      */
    val setMidFlywheelSpeed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftTwo)
    setMidFlywheelSpeed.foreach(() => {
      curFlywheelSpeedLeft = config.get.shooterFlywheel.props.midShootSpeedLeft
      curFlywheelSpeedRight = config.get.shooterFlywheel.props.midShootSpeedRight
    })

    /**
      * Flywheel speed set to high speed
      * LeftThree pressed
      */
    val setHighFlywheelSpeed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftThree)
    setHighFlywheelSpeed.foreach(() => {
      curFlywheelSpeedLeft = config.get.shooterFlywheel.props.fastShootSpeedLeft
      curFlywheelSpeedRight = config.get.shooterFlywheel.props.fastShootSpeedRight
    })

    /**
      * Runs flywheel at set speed
      * LeftFour pressed
      */
    val runFlywheelPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftFour)
    runFlywheelPressed.foreach(new WhileAtDoubleVelocity(
      flywheelSpeedLeft, flywheelSpeedRight, RevolutionsPerMinute(0)).apply(new ContinuousTask {
      override protected def onEnd() = {}

      override protected def onStart() = {}
    }))

    /**
      * Uses toggle to determine flywheel speed
      * Overrides set flywheel speed
      * LeftFive pressed
      */
    val flywheelOverridePressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftFive)
    flywheelOverridePressed.foreach(
      new WhileAtVelocity(
        driverHardware.operatorJoystick.y.map(_.toEach * shooterFlywheelProps.get.maxVelocityLeft),
        RevolutionsPerMinute(0)).toContinuous)
  }

  gearGrabber.zip(gearTilter).foreach { t =>
    implicit val (grabber, tilt) = t

    val bothPressed = Signal(driverHardware.operatorJoystick.getRawButton(JoystickButtons.RightFour)
      && driverHardware.operatorJoystick.getRawButton(JoystickButtons.RightFive)).filter(identity)

    val onlyRightFourPressed = Signal(driverHardware.operatorJoystick.getRawButton(JoystickButtons.RightFour)
      && !driverHardware.operatorJoystick.getRawButton(JoystickButtons.RightFive)).filter(identity)

    val onlyRightFivePressed = Signal(!driverHardware.operatorJoystick.getRawButton(JoystickButtons.RightFour)
      && driverHardware.operatorJoystick.getRawButton(JoystickButtons.RightFive)).filter(identity)

    /**
      * Releases gear
      * only RightFour pressed
      */
    onlyRightFourPressed.foreach(new OpenGrabber)

    /**
      * extends collector
      * only RightFive pressed
      */
    onlyRightFivePressed.foreach(new ExtendTilter)

    /**
      * Extends collector and opens grabber
      * both RightFour and RightFive pressed
      */
    bothPressed.foreach(
      new OpenGrabber().and(new ExtendTilter())
    )
  }

  collectorElevator.zip(collectorRollers).zip(collectorExtender).zip(loadTray).foreach { t =>
    implicit val (((elevator, roller), extend), loadTray) = t

    /**
      * Collects fuel
      * Collects at high speed
      * Trigger for driver joystick pressed
      */
    val driverCollectFuelPressed = driverHardware.driverJoystick.buttonPressed(JoystickButtons.Trigger)
    driverCollectFuelPressed.foreach(CollectorTasks.collect(collectorRollersProps.map(_.highRollerSpeedOutput)))

    /**
      * Collects fuel
      * Collects at high speed
      * RightFour pressed
      */
    val collectFuelPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.RightFour)
    collectFuelPressed.foreach(CollectorTasks.collect(collectorRollersProps.map(_.highRollerSpeedOutput)))

    /**
      * Collects fuel
      * Collects at slow speed
      * RightThree pressed
      */
    val slowCollectFuelPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.RightThree)
    slowCollectFuelPressed.foreach(CollectorTasks.collect(collectorRollersProps.map(_.lowRollerSpeedOutput)))
  }

  climberPuller.foreach { t =>
    implicit val (pull) = t
    /**
      * Climbs
      * Both trigger bottoms for operator joystick and driver joystick pressed
      */
    val climbPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.TriggerBottom) &&
      driverHardware.driverJoystick.buttonPressed(JoystickButtons.TriggerBottom)
    climbPressed.foreach(ClimberTasks.climb.and(new SelectCamera(LeftCam)))

  }

  r.agitator.foreach { implicit a =>

    /**
      * Runs agitator counterclockwise
      * RightOne pressed
      */
    val runAgitatorCounterclockwisePressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.RightOne)
    runAgitatorCounterclockwisePressed.
      foreach(new SpinAgitator)
  }

  r.loadTray.foreach { implicit l =>
    driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftSix).
      foreach(new ExtendTray())
  }
}
