package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.seventeen.shooter.flywheel.velocityTasks.SpinAtVelocity
import com.lynbrookrobotics.seventeen.shooter.ShooterTasks
import com.lynbrookrobotics.potassium.frc.Implicits._
import com.lynbrookrobotics.seventeen.agitator.SpinAgitator
import com.lynbrookrobotics.seventeen.climber.ClimberTasks
import com.lynbrookrobotics.seventeen.collector.CollectorTasks
import com.lynbrookrobotics.seventeen.collector.rollers.RollBallsInCollector
import com.lynbrookrobotics.seventeen.gear.GearTasks
import com.lynbrookrobotics.seventeen.gear.grabber.OpenGrabber
import com.lynbrookrobotics.seventeen.shooter.shifter.{ShiftShooter, ShooterShiftLeft, ShooterShiftRight}
import squants.time.{Frequency, RevolutionsPerMinute}

class ButtonMappings(r: CoreRobot) {
  import r._

  var curFlywheelSpeed: Frequency = if (config.get.shooterFlywheel != null) {
    config.get.shooterFlywheel.props.midShootSpeed
  } else RevolutionsPerMinute(0)

  val flywheelSpeed = Signal(curFlywheelSpeed)

  shooterFlywheel.zip(shooterFeeder).zip(shooterShifter).zip(agitator).foreach { t =>
    implicit val (((fly, feed), shift), agitator) = t

    /**
      * Shoots fuel at high speed
      * Elevates at high speed
      * Trigger pressed
      */
    val shootFuelPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.Trigger).
      and(!driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftFive))
    shootFuelPressed.foreach(ShooterTasks.continuousShoot(flywheelSpeed))

    /**
      * Shoots fuel at low speed
      * Elevates at slow speed
      * Trigger and LeftFive pressed
      */
    val slowShootFuelPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.Trigger).
      and(driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftFive))
    slowShootFuelPressed.foreach(ShooterTasks.continuousShootSlowly(flywheelSpeed))

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
  }

  shooterFlywheel.foreach { implicit fly =>
    /**
      * Flywheel speed set to low speed
      * LeftOne pressed
      */
    val setLowFlywheelSpeed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftOne)
    setLowFlywheelSpeed.foreach(() => curFlywheelSpeed = config.get.shooterFlywheel.props.lowShootSpeed)

    /**
      * Flywheel speed set to medium speed
      * LeftTwo pressed
      */
    val setMidFlywheelSpeed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftTwo)
    setMidFlywheelSpeed.foreach(() => curFlywheelSpeed = config.get.shooterFlywheel.props.midShootSpeed)

    /**
      * Flywheel speed set to high speed
      * LeftThree pressed
      */
    val setHighFlywheelSpeed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftThree)
    setHighFlywheelSpeed.foreach(() => curFlywheelSpeed = config.get.shooterFlywheel.props.fastShootSpeed)

    /**
      * Runs flywheel at set speed
      * LeftFour pressed and POV pushed up
      */
    val runFlywheelPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftFour)
      .and(Signal(driverHardware.operatorJoystick.getPOV() == 0).filter(identity))
    runFlywheelPressed.foreach(new SpinAtVelocity(flywheelSpeed))
  }

  gearGrabber.zip(gearTilter).foreach { t =>
    implicit val (grabber, tilt) = t

    /**
      * Grabs gear
      * RightFive pressed
      */
    val grabGearPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.RightFive)
    grabGearPressed.foreach(GearTasks.loadGearFromGroundAbortable(JoystickButtons.RightFive).toContinuous)

    /**
      * Releases gear
      * RightSix pressed
      */
    val releaseGearPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.RightSix)
    releaseGearPressed.foreach(new OpenGrabber)
  }

  collectorElevator.zip(collectorRollers).zip(collectorExtender).foreach { t =>
    implicit val ((elevator, roller), extend) = t

    /**
      * Runs fuel collector opposite direction
      * LeftFour pressed and POV pushed down
      */
    val runFuelCollectorOutPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftFour)
      .and(Signal(driverHardware.operatorJoystick.getPOV() == 180).filter(down => down))
    runFuelCollectorOutPressed.foreach(new RollBallsInCollector(collectorRollersProps.map(-_.highRollerSpeedOutput)))

    /**
      * Collects fuel
      * Collects at high speed
      * LeftSix pressed
      */
    val collectFuelPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftSix)
    collectFuelPressed.foreach(CollectorTasks.collect(collectorRollersProps.map(_.highRollerSpeedOutput)))

    /**
      * Collects fuel
      * Collects at slow speed
      * LeftSix pressed
      */
    val slowCollectFuelPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftSix)
      .and(driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftFive))
    slowCollectFuelPressed.foreach(CollectorTasks.collect(collectorRollersProps.map(_.lowRollerSpeedOutput)))
  }

  climberPuller.foreach { t =>
    implicit val pull = t
    /**
      * Climbs
      * Both operator joystick and driver joystick pressed
      */
    val climbPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.TriggerBottom)
      .and(driverHardware.driverJoystick.buttonPressed(2))
    climbPressed.foreach(ClimberTasks.climb)

  }

  r.agitator.foreach { t =>
    implicit val agitator = t
    /**
      * Runs agitator counterclockwise
      * LeftFour pressed and POV pushed right
      */
    val runAgitatorCounterclockwisePressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftFour)
      .and(Signal(driverHardware.operatorJoystick.getPOV() == 90).filter(down => down))
    runAgitatorCounterclockwisePressed.foreach(new SpinAgitator)
  }
}
