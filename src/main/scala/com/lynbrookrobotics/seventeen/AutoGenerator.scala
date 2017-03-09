package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.tasks.{FiniteTask, WaitTask}
import com.lynbrookrobotics.potassium.units.Point
import com.lynbrookrobotics.seventeen.agitator.Agitator
import com.lynbrookrobotics.seventeen.collector.elevator.CollectorElevator
import com.lynbrookrobotics.seventeen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.seventeen.gear.grabber.{GearGrabber, OpenGrabber, OpenGrabberUntilReleased}
import com.lynbrookrobotics.seventeen.shooter.ShooterTasks
import com.lynbrookrobotics.seventeen.shooter.flywheel.ShooterFlywheel
import squants.space.{Degrees, Feet, Inches}
import drivetrain.unicycleTasks._
import squants.motion.FeetPerSecond
import squants.time.Seconds
import drivetrain.Drivetrain

class AutoGenerator(r: CoreRobot) {
  import r._

  val robotLength = Inches(28.313 + 7 /* bumpers */)

  def centerGearAndCrossLine(implicit d: Drivetrain, g: GearGrabber): FiniteTask = {
    val initialTurnPosition = drivetrainHardware.turnPosition.get

    val relativeTurn = drivetrainHardware.turnPosition.map(_ - initialTurnPosition)

    val xyPosition = XYPosition(
      relativeTurn,
      drivetrainHardware.forwardPosition
    )

    new DriveDistanceStraight(
      Inches(109) - robotLength,
      Inches(3),
      Degrees(10)
    ).then(new FollowWayPointsWithPosition(
      Seq(
        new Point(
          Inches(0),
          Inches(109) - robotLength
        ),
        new Point(
          Inches(0),
          Inches(109) - robotLength - Feet(3)
        ),
        new Point(
          Feet(4),
          Inches(109) - robotLength - Feet(4)
        ),
        new Point(
          Feet(8),
          Inches(109) - robotLength - Feet(3)
        ),
        new Point(
          Feet(8),
          Inches(109) - robotLength + Feet(9)
        )
      ),
      Feet(0.1),
      xyPosition,
      relativeTurn
    ).andUntilDone(new OpenGrabber))
  }

  def shootCenterGearAndCrossLine(implicit d: Drivetrain,
                                  g: GearGrabber,
                                  ce: CollectorElevator,
                                  cr: CollectorRollers,
                                  a: Agitator,
                                  f: ShooterFlywheel): FiniteTask = {
    new WaitTask(Seconds(3)).andUntilDone(
      ShooterTasks.continuousShoot(
        shooterFlywheelProps.map(_.midShootSpeedLeft),
        shooterFlywheelProps.map(_.midShootSpeedRight)
      )
    ).then(centerGearAndCrossLine)
  }

  def rightGearAndCrossLine(implicit  drivetrain: Drivetrain, gearGrabber: GearGrabber): FiniteTask = {
    val initialTurnPosition = drivetrainHardware.turnPosition.get

    val relativeTurn = drivetrainHardware.turnPosition.map(_ - initialTurnPosition)

    val xyPosition = XYPosition(
      relativeTurn,
      drivetrainHardware.forwardPosition
    )

    new FollowWayPointsWithPosition(
      Seq(
        new Point(
          Feet(0),
          Feet(15.087)
        ),
        new Point(
          Feet(15.087 + 2),
          Feet(-1.5)
        ),
        new Point(
          Feet(15.087),
          Feet(-3.017)
        )
      ),
      Feet(0.1),
      xyPosition,
      relativeTurn
    ).then(new DriveDistanceStraight(
      Feet(-3),
      Feet(0.1),
      Degrees(5))).andUntilDone(new OpenGrabber)
  }

  def leftGearAndCrossLine(implicit  d: Drivetrain, gearGrabber: GearGrabber): FiniteTask = {
    val initialTurnPosition = drivetrainHardware.turnPosition.get

    val relativeTurn = drivetrainHardware.turnPosition.map(_ - initialTurnPosition)

    val xyPosition = XYPosition(
      relativeTurn,
      drivetrainHardware.forwardPosition
    )

    new FollowWayPointsWithPosition(
      Seq(
        new Point(
          Feet(0),
          Feet(15.087) - robotLength
        ),
        new Point(
          Feet(1.5),
          Feet(15.087 + 2) - robotLength
        ),
        new Point(
          Feet(3.017),
          Feet(15.087) - robotLength
        )
      ),
      Feet(0.1),
      xyPosition,
      relativeTurn
    ).then(new DriveDistanceStraight(
      Feet(-3),
      Feet(0.1),
      Degrees(5))).andUntilDone(new OpenGrabber)
  }
}
