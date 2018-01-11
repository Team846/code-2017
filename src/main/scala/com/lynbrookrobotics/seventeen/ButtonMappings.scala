package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.frc.Implicits._
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.seventeen.agitator.SpinAgitator
import com.lynbrookrobotics.seventeen.camselect._
import com.lynbrookrobotics.seventeen.climber.puller.RunPuller
import com.lynbrookrobotics.seventeen.collector.CollectorTasks
import com.lynbrookrobotics.seventeen.collector.extender.CollectorExtenderExtended
import com.lynbrookrobotics.seventeen.gear.grabber.OpenGrabber
import com.lynbrookrobotics.seventeen.gear.tilter.ExtendTilter
import com.lynbrookrobotics.seventeen.loadtray.ExtendTray
import com.lynbrookrobotics.seventeen.shooter.ShooterTasks
import com.lynbrookrobotics.seventeen.shooter.flywheel.velocityTasks.{WhileAtDoubleVelocity, WhileAtVelocity}
import com.lynbrookrobotics.seventeen.shooter.shifter.{ShooterShiftLeft, ShooterShiftRight}
import squants.Percent
import squants.time._

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

//  for {
//    shooterFlywheel <- shooterFlywheel
//    collectorElevator <- collectorElevator
//    collectorRollers <- collectorRollers
//    shooterShifter <- shooterShifter
//    agitator <- agitator
//    collectorExtender <- collectorExtender
//    loadTray <- loadTray
//  } {
//    /**
//      * Shoots fuel at high speed
//      * Trigger pressed
//      */
//    val shootFuelPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.Trigger)
//    shootFuelPressed.foreach(ShooterTasks.continuousShoot(flywheelTargetLeft, flywheelTargetRight)(
//      collectorElevator, collectorRollers, agitator, shooterFlywheel, collectorExtender, loadTray
//    ))
//
//    /**
//      * Shifts shooter to left
//      * TriggerLeft pressed
//      */
//    val shiftShooterLeftPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.TriggerLeft)
//    shiftShooterLeftPressed.foreach(() => shooterShifter.currentState = ShooterShiftLeft)
//
//    /**
//      * Shifts shooter to right
//      * TriggerRight pressed
//      */
//    val shiftShooterRightPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.TriggerRight)
//    shiftShooterRightPressed.foreach(() => shooterShifter.currentState = ShooterShiftRight)
//
//    driverHardware.operatorJoystick.buttonPressed(JoystickButtons.RightTwo)
//      .foreach(new ContinuousTask {
//        override protected def onStart(): Unit = {
//          agitator.setController(coreTicks.mapToConstant(Percent(50)))
//          collectorElevator.setController(coreTicks.mapToConstant(-Percent(50)))
//          collectorRollers.setController(coreTicks.mapToConstant(-Percent(50)))
//          collectorExtender.setController(coreTicks.mapToConstant(CollectorExtenderExtended))
//        }
//
//        override protected def onEnd(): Unit = {
//          agitator.resetToDefault()
//          collectorElevator.resetToDefault()
//          collectorRollers.resetToDefault()
//          collectorExtender.resetToDefault()
//        }
//      })
//  }

//  for {
//    shooterFlywheel <- shooterFlywheel
//  } {
//    /**
//      * Flywheel speed set to low speed
//      * LeftOne pressed
//      */
//    val setLowFlywheelSpeed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftOne)
//    setLowFlywheelSpeed.foreach(() => {
//      curFlywheelTargetLeft = config.get.shooterFlywheel.props.lowShootSpeedLeft
//      curFlywheelTargetRight = config.get.shooterFlywheel.props.lowShootSpeedRight
//    })
//
//    /**
//      * Flywheel speed set to medium speed
//      * LeftTwo pressed
//      */
//    val setMidFlywheelSpeed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftTwo)
//    setMidFlywheelSpeed.foreach(() => {
//      curFlywheelTargetLeft = config.get.shooterFlywheel.props.midShootSpeedLeft
//      curFlywheelTargetRight = config.get.shooterFlywheel.props.midShootSpeedRight
//    })
//
//    /**
//      * Flywheel speed set to high speed
//      * LeftThree pressed
//      */
//    val setHighFlywheelSpeed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftThree)
//    setHighFlywheelSpeed.foreach(() => {
//      curFlywheelTargetLeft = config.get.shooterFlywheel.props.fastShootSpeedLeft
//      curFlywheelTargetRight = config.get.shooterFlywheel.props.fastShootSpeedRight
//    })
//
//    /**
//      * Runs flywheel at set speed
//      * LeftFour pressed
//      */
//    val runFlywheelPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftFour)
//    runFlywheelPressed.foreach(new WhileAtDoubleVelocity(
//      flywheelTargetLeft, flywheelTargetRight, RevolutionsPerMinute(0)
//    )(shooterFlywheel).toContinuous)
//
//    /**
//      * Uses toggle to determine flywheel speed
//      * Overrides set flywheel speed
//      * LeftFive pressed
//      */
//    val flywheelOverridePressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftFive)
//    flywheelOverridePressed.foreach(
//      new WhileAtVelocity(
//        driverHardware.joystickStream.map(s => s.operator.y.toEach * shooterFlywheelProps.get.maxVelocityLeft),
//        RevolutionsPerMinute(0)
//      )(shooterFlywheel).toContinuous)
//  }

//  for {
//    gearGrabber <- gearGrabber
//    gearTilter <- gearTilter
//  } {
//    val bothPressed = Signal(driverHardware.operatorJoystick.getRawButton(JoystickButtons.RightFour)
//      && driverHardware.operatorJoystick.getRawButton(JoystickButtons.RightFive)).filter(identity)
//
//    val onlyRightFourPressed = Signal(driverHardware.operatorJoystick.getRawButton(JoystickButtons.RightFour)
//      && !driverHardware.operatorJoystick.getRawButton(JoystickButtons.RightFive)).filter(identity)
//
//    val onlyRightFivePressed = Signal(!driverHardware.operatorJoystick.getRawButton(JoystickButtons.RightFour)
//      && driverHardware.operatorJoystick.getRawButton(JoystickButtons.RightFive)).filter(identity)
//
//    /**
//      * Releases gear
//      * only RightFour pressed
//      */
//    onlyRightFourPressed.foreach(new OpenGrabber(gearGrabber))
//
//    /**
//      * Extends tilter
//      * only RightFive pressed
//      */
//    onlyRightFivePressed.foreach(new ExtendTilter(gearTilter))
//
//    /**
//      * Extends tilter and opens grabber
//      * both RightFour and RightFive pressed
//      */
//    bothPressed.foreach(
//      new OpenGrabber(gearGrabber).and(new ExtendTilter(gearTilter))
//    )
//  }

//  for {
//    collectorElevator <- collectorElevator
//    collectorRollers <- collectorRollers
//    collectorExtender <- collectorExtender
//    loadTray <- loadTray
//  } {
//    val highRollTargetStream = r.coreTicks.map(_ => collectorRollersProps.get.highRollerSpeedOutput)
//
//    val purgeTargetStream = r.coreTicks.map(_ => -collectorRollersProps.get.highRollerSpeedOutput)
//    val purgeSlowTargetStream = r.coreTicks.map(_ => -collectorRollersProps.get.highRollerSpeedOutput * 0.5)
//
//    /**
//      * Collects fuel
//      * Collects at high speed
//      * Trigger for driver joystick pressed
//      */
//    val driverCollectFuelPressed = driverHardware.driverJoystick.buttonPressed(JoystickButtons.Trigger)
//    driverCollectFuelPressed.foreach(CollectorTasks.collect(
//      highRollTargetStream
//    )(collectorExtender, collectorElevator, collectorRollers, loadTray))
//
//    /**
//      * Collects fuel
//      * Collects at high speed
//      * RightFour pressed
//      */
//    val collectFuelPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.RightFour)
//    collectFuelPressed.foreach(CollectorTasks.collect(
//      highRollTargetStream
//    )(collectorExtender, collectorElevator, collectorRollers, loadTray))
//
//    /**
//      * Collects fuel
//      * Collects at slow speed
//      * RightThree pressed
//      */
//    val slowCollectFuelPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.RightThree)
//    val slowRollTargetStream = r.coreTicks.map(_ => collectorRollersProps.get.lowRollerSpeedOutput)
//     slowCollectFuelPressed.foreach(CollectorTasks.collect(
//       slowRollTargetStream
//     )(collectorExtender, collectorElevator, collectorRollers, loadTray))
//
//    /**
//      * Purges fuel
//      * Purges at high speed
//      * TriggerLeft pressed
//      */
//    val purgeFuelPressed = driverHardware.driverJoystick.buttonPressed(JoystickButtons.TriggerLeft)
//    purgeFuelPressed.foreach(CollectorTasks.collect(
//      purgeTargetStream
//    )(collectorExtender, collectorElevator, collectorRollers, loadTray))
//
//    /**
//      * Purges fuel
//      * Purges at half speed
//      * TriggerRight pressed
//      */
//    val purgeFuelSlowPressed = driverHardware.driverJoystick.buttonPressed(JoystickButtons.TriggerRight)
//    purgeFuelSlowPressed.foreach(CollectorTasks.collect(
//      purgeSlowTargetStream
//    )(collectorExtender, collectorElevator, collectorRollers, loadTray))
//  }

//  for {
//    climberPuller <- climberPuller
//  } {
//    /**
//      * Climbs
//      * Both trigger bottoms for operator joystick and driver joystick pressed
//      */
//    val climbPressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.TriggerBottom) &&
//      driverHardware.driverJoystick.buttonPressed(JoystickButtons.TriggerBottom)
//    climbPressed.foreach(new RunPuller(climberPuller).and(new SelectCamera(LeftCam)))
//
//  }

//  for {
//    agitator <- agitator
//  } {
//    /**
//      * Runs agitator counterclockwise
//      * RightOne pressed
//      */
//    val runAgitatorCounterclockwisePressed = driverHardware.operatorJoystick.buttonPressed(JoystickButtons.RightOne)
//    runAgitatorCounterclockwisePressed.
//      foreach(new SpinAgitator(agitator))
//  }

//  for {
//    loadTray <- loadTray
//  } {
//    driverHardware.operatorJoystick.buttonPressed(JoystickButtons.LeftSix).
//      foreach(new ExtendTray(loadTray))
//  }
}
