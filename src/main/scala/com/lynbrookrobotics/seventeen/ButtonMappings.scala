package com.lynbrookrobotics.seventeen

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
import squants.time._
import com.lynbrookrobotics.seventeen.gear.tilter.ExtendTilter
import com.lynbrookrobotics.seventeen.shooter.flywheel.ShooterFlywheelProperties
import com.lynbrookrobotics.seventeen.shooter.shifter.{ShiftShooter, ShooterShiftLeft, ShooterShiftRight}
import com.lynbrookrobotics.potassium.streams.Stream

class ButtonMappings(r: CoreRobot) {

  import r._

  var curFlywheelTargetLeft: Frequency = if (config.get.shooterFlywheel != null) {
    config.get.shooterFlywheel.props.midShootSpeedLeft
  } else RevolutionsPerMinute(0)

  var curFlywheelTargetRight: Frequency = if (config.get.shooterFlywheel != null) {
    config.get.shooterFlywheel.props.midShootSpeedRight
  } else RevolutionsPerMinute(0)

  val flywheelTargetLeft = r.coreTicks.map(_ => curFlywheelTargetLeft)
  val flywheelTargetRight = r.coreTicks.map(_ => curFlywheelTargetRight)

  shooterFlywheel.zip(collectorElevator).zip(collectorRollers).zip(shooterShifter).zip(agitator).zip(collectorExtender).zip(loadTray).foreach { t =>
    implicit val ((((((fly, elev), roll), shift), agitator), ex), lt) = t

    /**
      * Shoots fuel at high speed
      * Trigger pressed
      */
    val shootFuelPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.Trigger)
    shootFuelPressed.foreach(ShooterTasks.continuousShoot(flywheelTargetLeft, flywheelTargetRight))

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
          agitator.setController(coreTicks.mapToConstant(Percent(50)))
          elev.setController(coreTicks.mapToConstant(-Percent(50)))
          roll.setController(coreTicks.mapToConstant(-Percent(50)))
          ex.setController(coreTicks.mapToConstant(CollectorExtenderExtended))
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
      curFlywheelTargetLeft = config.get.shooterFlywheel.props.lowShootSpeedLeft
      curFlywheelTargetRight = config.get.shooterFlywheel.props.lowShootSpeedRight
    })

    /**
      * Flywheel speed set to medium speed
      * LeftTwo pressed
      */
    val setMidFlywheelSpeed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftTwo)
    setMidFlywheelSpeed.foreach(() => {
      curFlywheelTargetLeft = config.get.shooterFlywheel.props.midShootSpeedLeft
      curFlywheelTargetRight = config.get.shooterFlywheel.props.midShootSpeedRight
    })

    /**
      * Flywheel speed set to high speed
      * LeftThree pressed
      */
    val setHighFlywheelSpeed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftThree)
    setHighFlywheelSpeed.foreach(() => {
      curFlywheelTargetLeft = config.get.shooterFlywheel.props.fastShootSpeedLeft
      curFlywheelTargetRight = config.get.shooterFlywheel.props.fastShootSpeedRight
    })

    /**
      * Runs flywheel at set speed
      * LeftFour pressed
      */
    val runFlywheelPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftFour)
    runFlywheelPressed.foreach(new WhileAtDoubleVelocity(
      flywheelTargetLeft, flywheelTargetRight, RevolutionsPerMinute(0)).apply(new ContinuousTask {
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
        driverHardware.joystickStream.map(s => s.operator.y.toEach * shooterFlywheelProps.get.maxVelocityLeft),
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

    val highRollTargetStream = r.coreTicks.map(_ => collectorRollersProps.get.highRollerSpeedOutput)

    val purgeTargetStream = r.coreTicks.map(_ => -collectorRollersProps.get.highRollerSpeedOutput)
    val purgeSlowTargetStream = r.coreTicks.map(_ => -collectorRollersProps.get.highRollerSpeedOutput * 0.5)

    /**
      * Collects fuel
      * Collects at high speed
      * Trigger for driver joystick pressed
      */
    val driverCollectFuelPressed = driverHardware.driverJoystick.buttonPressed(JoystickButtons.Trigger)
    driverCollectFuelPressed.foreach(CollectorTasks.collect(highRollTargetStream))

    /**
      * Collects fuel
      * Collects at high speed
      * RightFour pressed
      */
    val collectFuelPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.RightFour)
    collectFuelPressed.foreach(CollectorTasks.collect(highRollTargetStream))

    /**
      * Collects fuel
      * Collects at slow speed
      * RightThree pressed
      */
    val slowCollectFuelPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.RightThree)
    val slowRollTargetStream = r.coreTicks.map(_ => collectorRollersProps.get.lowRollerSpeedOutput)
     slowCollectFuelPressed.foreach(CollectorTasks.collect(slowRollTargetStream))

    /**
      * Purges fuel
      * Purges at high speed
      * TriggerLeft pressed
      */
    val purgeFuelPressed = driverHardware.driverJoystick.buttonPressed(JoystickButtons.TriggerLeft)
    purgeFuelPressed.foreach(CollectorTasks.collect(purgeTargetStream))

    /**
      * Purges fuel
      * Purges at half speed
      * TriggerRight pressed
      */
    val purgeFuelSlowPressed = driverHardware.driverJoystick.buttonPressed(JoystickButtons.TriggerRight)
    purgeFuelSlowPressed.foreach(CollectorTasks.collect(purgeSlowTargetStream))
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
