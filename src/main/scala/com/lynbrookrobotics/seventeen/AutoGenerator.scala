package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.tasks.{FiniteTask, WaitTask}
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
import squants.space.{Degrees, Feet, Inches}
import drivetrain.unicycleTasks._
import squants.motion.FeetPerSecond
import squants.time.Seconds
import drivetrain.Drivetrain
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
        Inches(125) - (robotLength / 2) - Inches(40),
        Inches(3),
        Degrees(10),
        Percent(30)
      ).withTimeout(Seconds(8)).then(new RotateByAngle(
        Degrees(-45),
        Degrees(5)
      ).withTimeout(Seconds(5)).then(new DriveDistanceStraight(
        math.sqrt(2) * Inches(40) + Inches(4) - (robotLength / 2),
        Inches(3),
        Degrees(10),
        Percent(30)
      )))
    )
  }

  def leftGear(implicit d: Drivetrain, g: GearGrabber, t: GearTilter): FiniteTask = {
    toGearAndDrop(
      new DriveDistanceStraight(
        Inches(125) - (robotLength / 2) - Inches(30),
        Inches(3),
        Degrees(10),
        Percent(30)
      ).withTimeout(Seconds(8)).then(new RotateByAngle(
        Degrees(45),
        Degrees(5)
      ).withTimeout(Seconds(5)).then(new DriveDistanceStraight(
        math.sqrt(2) * Inches(30) + Inches(4) - (robotLength / 2),
        Inches(3),
        Degrees(10),
        Percent(30)
      )))
    )
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

  def shootCenterGearAndCrossLine(implicit d: Drivetrain,
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
    ).then(centerGearAndCrossLine)
  }
}
