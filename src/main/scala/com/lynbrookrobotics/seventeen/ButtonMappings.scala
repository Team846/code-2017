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
import squants.motion.{AngularVelocity, DegreesPerSecond}
import com.lynbrookrobotics.seventeen.agitator.Agitator
import com.lynbrookrobotics.seventeen.climber.puller
import com.lynbrookrobotics.seventeen.collector.elevator.{CollectorElevatorProperties, LoadIntoStorage}
import com.lynbrookrobotics.seventeen.collector.{elevator, extender, rollers}
import com.lynbrookrobotics.seventeen.gear.{grabber, tilter}
import com.lynbrookrobotics.seventeen.shooter.flywheel.ShooterFlywheelProperties
import com.lynbrookrobotics.seventeen.shooter.{feeder, flywheel, shifter}


sealed trait ShooterFlywheelState {
  val speed: AngularVelocity
}

class ButtonMappings(r: CoreRobot) {
  import r._

  case class LowSpeed(v: AngularVelocity) extends ShooterFlywheelState {
    override val speed: AngularVelocity = v
  }

  case class MidSpeed(v: AngularVelocity) extends ShooterFlywheelState {
    override val speed: AngularVelocity = v
  }

  case class HighSpeed(v: AngularVelocity) extends ShooterFlywheelState {
    override val speed: AngularVelocity = v
  }

  var flywheelSpeed: ShooterFlywheelState = new HighSpeed(DegreesPerSecond(10))

  shooterFlywheel.zip(shooterFeeder).zip(shooterShifter).zip(agitator).foreach { t =>
    implicit val (((fly, feed), shift), agitator) = t

    /**
      * Shoots fuel
      * Trigger pressed
      */
    val shootFuelPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.Trigger)
    shootFuelPressed.foreach(ShooterTasks.continuousShoot(flywheelSpeed))

    /**
      * Shifts shooter to left
      * TriggerLeft pressed
      */
    val shiftShooterLeftPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.TriggerLeft)
    shiftShooterLeftPressed.foreach(new ShiftShooter(ShooterShiftLeft))

    /**
      * Shifts shooter to right
      * TriggerRight pressed
      */
    val shiftShooterRightPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.TriggerRight)
    shiftShooterRightPressed.foreach(new ShiftShooter(ShooterShiftRight))
    /**
      * Flywheel speed set to low speed
      * LeftOne pressed
      */
    val setLowFlywheelSpeed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftOne)
    setLowFlywheelSpeed.foreach(() => flywheelSpeed = new LowSpeed(DegreesPerSecond(1)))

    /**
      * Flywheel speed set to medium speed
      * LeftTwo pressed
      */
    val setMidFlywheelSpeed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftTwo)
    setMidFlywheelSpeed.foreach(() => flywheelSpeed = new MidSpeed(DegreesPerSecond(5)))

    /**
      * Flywheel speed set to high speed
      * LeftThree pressed
      */
    val setHighFlywheelSpeed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftThree)
    setHighFlywheelSpeed.foreach(() => flywheelSpeed = new HighSpeed(DegreesPerSecond(10)))

    /**
      * Runs flywheel at set speed
      * LeftFour pressed
      */
    val runFlywheelPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftFour)
    runFlywheelPressed.foreach(new SpinAtVelocity(flywheelSpeed.speed))

    /**
      * Overrides set flywheel speed
      * Uses throttle to control flywheel speed
      * LeftFive pressed
      */
    val flywheelOverridePressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftFive)
    flywheelOverridePressed.foreach(new SpinAtVelocity(driverHardware.operatorJoystick.y.get.toEach * shooterFlywheelProps.get.fastShootSpeed))
  }

  gearGrabber.zip(gearTilter).foreach { t =>
    implicit val (grabber, tilt) = t

    /**
      * Grabs gear
      * RightFive pressed
      */
    val grabGearPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.RightFive)
    grabGearPressed.foreach(GearTasks.loadGearFromGroundAbortable(7, JoystickButtons.RightOne, JoystickButtons.RightFive, lighting.get).toContinuous)

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
      * Collects fuel slowly
      * RightThree pressed
      */
    val collectFuelSlowPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.RightThree)
    collectFuelSlowPressed.foreach(CollectorTasks.collect(collectorRollersProps.map(_.lowRollerSpeedOutput)))

    /**
      * Collects fuel
      * RightFour pressed
      */
    val collectFuelPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.RightFour)
    collectFuelPressed.foreach(CollectorTasks.collect(collectorRollersProps.map(_.highRollerSpeedOutput)))

    /**
      * Collects fuel
      * Trigger for driver joystick pressed
      */
    val driverCollectFuelPressed = driverHardware.driverJoystick.buttonPressed(JoystickButtons.Trigger)
    driverCollectFuelPressed.foreach(CollectorTasks.collect(collectorRollersProps.map(_.highRollerSpeedOutput)))
  }

  climberPuller.foreach { t =>
    implicit val pull = t
    /**
      * Climbs
      * Both Trigger Bottoms for the operator joystick and driver joystick pressed
      */
    val climbPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.TriggerBottom)
      .and(driverHardware.driverJoystick.buttonPressed(JoystickButtons.TriggerBottom))
    climbPressed.foreach(ClimberTasks.climb)
  }

  r.agitator.foreach { t =>
    implicit val agitator = t
    /**
      * Runs agitator counterclockwise
      * RightOne pressed
      */
    val runAgitatorCounterclockwisePressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.RightOne)
    runAgitatorCounterclockwisePressed.foreach(new SpinAgitator)
  }
}
