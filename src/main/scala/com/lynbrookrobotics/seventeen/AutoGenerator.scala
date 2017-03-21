package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.tasks.{ContinuousTask, FiniteTask, WaitTask}
import com.lynbrookrobotics.potassium.units.Point
import com.lynbrookrobotics.seventeen.agitator.Agitator
import com.lynbrookrobotics.seventeen.collector.elevator.CollectorElevator
import com.lynbrookrobotics.seventeen.collector.extender.CollectorExtender
import com.lynbrookrobotics.seventeen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.seventeen.gear.GearTasks
import com.lynbrookrobotics.seventeen.gear.grabber.{CloseGrabber, GearGrabber, OpenGrabber, OpenGrabberUntilReleased}
import com.lynbrookrobotics.seventeen.gear.tilter.{ExtendTilter, GearTilter, RetractTilter}
import com.lynbrookrobotics.seventeen.shooter.ShooterTasks
import com.lynbrookrobotics.seventeen.shooter.flywheel.ShooterFlywheel
import com.lynbrookrobotics.seventeen.shooter.shifter.{ShiftShooter, ShooterShiftLeft, ShooterShiftRight, ShooterShifter}
import squants.space.{Degrees, Feet, Inches}
import drivetrain.unicycleTasks._
import squants.motion.FeetPerSecond
import squants.time.Seconds
import drivetrain.{Drivetrain, DrivetrainHardware}
import squants.Percent

class AutoGenerator(r: CoreRobot) {
  import r._

  val robotLength = Inches(28.313 + 7 /* bumpers */)

  val gearPegDistance = Inches(109)

  def slowCrossLine(implicit d: Drivetrain): FiniteTask = {
    new DriveDistanceStraight(
      Inches(107) - robotLength,
      Inches(3),
      Degrees(10),
      Percent(20)
    ).withTimeout(Seconds(8))
  }

  def toGearAndDrop(driveTo: FiniteTask)(implicit d: Drivetrain, g: GearGrabber, t: GearTilter): FiniteTask = {
    val dropAndBack = new WaitTask(Seconds(1)).then(new DriveDistanceStraight(
      -Feet(2),
      Inches(3),
      Degrees(10),
      Percent(30)
    )).withTimeout(Seconds(5)).andUntilDone(
      new OpenGrabber() and new ExtendTilter()
    )

    driveTo.then(dropAndBack)
  }

  def centerGear(implicit d: Drivetrain, g: GearGrabber, t: GearTilter): FiniteTask = {
    toGearAndDrop(
      new DriveDistanceStraight(
        gearPegDistance - robotLength,
        Inches(3),
        Degrees(10),
        Percent(30)
      ).withTimeout(Seconds(8))
    )
  }

  def rightGear(implicit d: Drivetrain, g: GearGrabber, t: GearTilter): FiniteTask = {
    toGearAndDrop(
      new DriveDistanceStraight(
        Inches(90.5),
        Inches(3),
        Degrees(10),
        Percent(30)
      ).withTimeout(Seconds(8)).then(new RotateByAngle(
        Degrees(-60),
        Degrees(5)
      ).withTimeout(Seconds(5)).then(new DriveDistanceStraight(
        Inches(45.9),
        Inches(3),
        Degrees(10),
        Percent(30)
      )))
    )
  }

  def leftGear(implicit d: Drivetrain, g: GearGrabber, t: GearTilter): FiniteTask = {
    toGearAndDrop(
      new DriveDistanceStraight(
        Inches(90.5),
        Inches(3),
        Degrees(10),
        Percent(30)
      ).withTimeout(Seconds(8)).then(new RotateByAngle(
        Degrees(60),
        Degrees(5)
      ).withTimeout(Seconds(5)).then(new DriveDistanceStraight(
        Inches(45.9),
        Inches(3),
        Degrees(10),
        Percent(30)
      )))
    )
  }

  def leftHopperAndShoot(implicit d: Drivetrain,
                         g: GearGrabber,
                         ce: CollectorElevator,
                         cr: CollectorRollers,
                         a: Agitator,
                         f: ShooterFlywheel,
                         t: GearTilter,
                         ex: CollectorExtender,
                         sh: ShooterShifter): ContinuousTask = {
    val initAngle = drivetrainHardware.turnPosition.get

    new DriveDistanceStraight(
      Inches(80.125),
      Inches(3),
      Degrees(10),
      Percent(30)
    ).withTimeout(Seconds(8)).then(new RotateByAngle(
      Degrees(-90),
      Degrees(2.5)
    ).withTimeout(Seconds(5))).then(
      new DriveDistanceStraight(
        Inches(27.4),
        Inches(3),
        Degrees(10),
        Percent(30)
      ).withTimeout(Seconds(8))
    ).then(new WaitTask(Seconds(0.5)).andUntilDone(new DriveOpenLoop(
      Signal.constant(Percent(20)),
      Signal.constant(Percent(0))
    ))).then(new RotateToAngle(
      Degrees(-90) + initAngle,
      Degrees(5)
    ).withTimeout(Seconds(2))).then(ShooterTasks.continuousShoot(
      shooterFlywheelProps.map(_.midShootSpeedLeft),
      shooterFlywheelProps.map(_.midShootSpeedRight)
    ).and(new ShiftShooter(Signal.constant(ShooterShiftLeft))))
  }

  def rightHopperAndShoot(implicit d: Drivetrain,
                          g: GearGrabber,
                          ce: CollectorElevator,
                          cr: CollectorRollers,
                          a: Agitator,
                          f: ShooterFlywheel,
                          t: GearTilter,
                          ex: CollectorExtender,
                          sh: ShooterShifter): ContinuousTask = {

    val initAngle = drivetrainHardware.turnPosition.get
    new DriveDistanceStraight(
      Inches(80.125),
      Inches(3),
      Degrees(10),
      Percent(30)
    ).withTimeout(Seconds(8)).then(new RotateByAngle(
      Degrees(90),
      Degrees(5)
    ).withTimeout(Seconds(5)).then(
      new DriveDistanceStraight(
        Inches(33.4),
        Inches(3),
        Degrees(10),
        Percent(30)
      ).withTimeout(Seconds(8))
    ).then(new WaitTask(Seconds(0.5)).andUntilDone(new DriveOpenLoop(
      Signal.constant(Percent(20)),
      Signal.constant(Percent(0))
    )))).then(new RotateToAngle(
      Degrees(90) + initAngle,
      Degrees(5)
    )).then(ShooterTasks.continuousShoot(
      shooterFlywheelProps.map(_.midShootSpeedLeft),
      shooterFlywheelProps.map(_.midShootSpeedRight)
    ).and(new ShiftShooter(Signal.constant(ShooterShiftRight))))
  }

  def centerGearAndCrossLine(implicit d: Drivetrain, g: GearGrabber, t: GearTilter): FiniteTask = {
    val initialTurnPosition = drivetrainHardware.turnPosition.get

    val relativeTurn = drivetrainHardware.turnPosition.map(_ - initialTurnPosition)

    val xyPosition = XYPosition(
      relativeTurn,
      drivetrainHardware.forwardPosition
    )

    toGearAndDrop(
      new DriveDistanceStraight(
        gearPegDistance - robotLength,
        Inches(3),
        Degrees(10),
        Percent(30)
      ).withTimeout(Seconds(8))
    ).then(new FollowWayPointsWithPosition(
      Seq(
        new Point(
          Inches(0),
          gearPegDistance - robotLength
        ),
        new Point(
          Inches(0),
          gearPegDistance - robotLength - Feet(3)
        ),
        new Point(
          Feet(4),
          gearPegDistance - robotLength - Feet(4)
        ),
        new Point(
          Feet(8),
          gearPegDistance - robotLength - Feet(3)
        ),
        new Point(
          Feet(8),
          gearPegDistance - robotLength + Feet(9)
        )
      ),
      Feet(0),
      xyPosition,
      relativeTurn
    ).withTimeout(Seconds(10)))
  }

  def shootCenterGear(implicit d: Drivetrain,
                                  g: GearGrabber,
                                  ce: CollectorElevator,
                                  cr: CollectorRollers,
                                  a: Agitator,
                                  f: ShooterFlywheel,
                                  t: GearTilter,
                                  ex: CollectorExtender): FiniteTask = {
    new WaitTask(Seconds(3)).andUntilDone(
      ShooterTasks.continuousShoot(
        shooterFlywheelProps.map(_.midShootSpeedLeft),
        shooterFlywheelProps.map(_.midShootSpeedRight)
      )
    ).then(centerGear)
  }

  def centerGearAndCrossLineSmooth(implicit d: Drivetrain, g: GearGrabber, t: GearTilter): FiniteTask = {
    val initialTurnPosition = drivetrainHardware.turnPosition.get

    val relativeTurn = drivetrainHardware.turnPosition.map(_ - initialTurnPosition)

    val xyPosition = XYPosition(
      relativeTurn,
      drivetrainHardware.forwardPosition
    )

    toGearAndDrop(
      new DriveDistanceSmooth(
        gearPegDistance - robotLength,
        FeetPerSecond(0)
      ).withTimeout(Seconds(8))
    ).then(new FollowWayPointsWithPosition(
      Seq(
        new Point(
          Inches(0),
          gearPegDistance - robotLength
        ),
        new Point(
          Inches(0),
          gearPegDistance - robotLength - Feet(3)
        ),
        new Point(
          Feet(4),
          gearPegDistance - robotLength - Feet(4)
        ),
        new Point(
          Feet(8),
          gearPegDistance - robotLength - Feet(3)
        ),
        new Point(
          Feet(8),
          gearPegDistance - robotLength + Feet(9)
        )
      ),
      Feet(0),
      xyPosition,
      relativeTurn
    ).withTimeout(Seconds(10)))
  }
}