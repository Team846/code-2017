package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.tasks.{ContinuousTask, FiniteTask, WaitTask}
import com.lynbrookrobotics.potassium.units.Point
import com.lynbrookrobotics.seventeen.agitator.Agitator
import com.lynbrookrobotics.seventeen.collector.elevator.CollectorElevator
import com.lynbrookrobotics.seventeen.collector.extender.CollectorExtender
import com.lynbrookrobotics.seventeen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.seventeen.drivetrain.Drivetrain
import com.lynbrookrobotics.seventeen.drivetrain.unicycleTasks._
import com.lynbrookrobotics.seventeen.gear.GearTasks
import com.lynbrookrobotics.seventeen.gear.roller.GearRoller
import com.lynbrookrobotics.seventeen.gear.tilter.{ExtendTilter, GearTilter}
import com.lynbrookrobotics.seventeen.loadtray.LoadTray
import com.lynbrookrobotics.seventeen.shooter.ShooterTasks
import com.lynbrookrobotics.seventeen.shooter.flywheel.ShooterFlywheel
import com.lynbrookrobotics.seventeen.shooter.shifter.{ShiftShooter, ShooterShiftLeft, ShooterShiftRight, ShooterShifter}
import squants.Percent
import squants.space.{Degrees, Feet, Inches}
import squants.time.Seconds

class AutoGenerator(r: CoreRobot) {
  import r._

  private val robotLength = Inches(35)

  private val gearPegDistance = Inches(109)

  def slowCrossLine(drivetrain: Drivetrain): FiniteTask = {
    new DriveDistanceStraight(
      Inches(107) - robotLength,
      Inches(3),
      Degrees(10),
      Percent(20)
    )(drivetrain).withTimeout(Seconds(8))
  }

  def toGearAndDrop(drivetrain: Drivetrain, gearRoller: GearRoller, gearTilter: GearTilter): FiniteTask = {
    val dropAndBack = new WaitTask(Seconds(1)).then(new DriveDistanceStraight(
      -Feet(2),
      Inches(3),
      Degrees(10),
      Percent(30)
    )(drivetrain)).withTimeout(Seconds(5)).andUntilDone(
      GearTasks.scoreGear(gearTilter, gearRoller)
    )

    dropAndBack
  }

  def centerGear(drivetrain: Drivetrain, gearRoller: GearRoller, gearTilter: GearTilter): FiniteTask = {
    new DriveDistanceStraight(
      gearPegDistance - robotLength,
      Inches(3),
      Degrees(10),
      Percent(30)
    )(drivetrain).withTimeout(Seconds(8)).then(
      toGearAndDrop(drivetrain, gearRoller, gearTilter)
    )
  }

  def rightGear(drivetrain: Drivetrain, gearRoller: GearRoller, gearTilter: GearTilter): FiniteTask = {
    new DriveDistanceStraight(
      Inches(90.5),
      Inches(3),
      Degrees(10),
      Percent(30)
    )(drivetrain).withTimeout(Seconds(8)).then(new RotateByAngle(
      Degrees(-57.61),
      Degrees(5),
      5
    )(drivetrain).withTimeout(Seconds(5))).then(new DriveDistanceStraight(
      Inches(19.75),
      Inches(3),
      Degrees(10),
      Percent(30)
    )(drivetrain).withTimeout(Seconds(5))).then(
      toGearAndDrop(drivetrain, gearRoller, gearTilter)
    )
  }

  def leftGear(drivetrain: Drivetrain, gearRoller: GearRoller, gearTilter: GearTilter): FiniteTask = {
    new DriveDistanceStraight(
      Inches(90.5),
      Inches(3),
      Degrees(10),
      Percent(30)
    )(drivetrain).withTimeout(Seconds(8)).then(new RotateByAngle(
      Degrees(57.61),
      Degrees(5),
      5
    )(drivetrain).withTimeout(Seconds(5))).then(new DriveDistanceStraight(
      Inches(19.75),
      Inches(3),
      Degrees(10),
      Percent(30)
    )(drivetrain).withTimeout(Seconds(5))).then(
      toGearAndDrop(drivetrain, gearRoller, gearTilter)
    )
  }

  def leftGearPurePursuit(drivetrain: Drivetrain,
                          gearRoller: GearRoller,
                          gearTilter: GearTilter): FiniteTask = {
    val relativeTurn = drivetrainHardware.turnPosition.relativize((init, curr) => {
      curr - init
    })

    val xyPosition = XYPosition(
      relativeTurn,
      drivetrainHardware.forwardPosition
    )
    new FollowWayPointsWithPosition(
      Seq(
        Point.origin,
        Point(
          Inches(0),
          Inches(90.5)
        ),
        Point(
          Inches(19.75) * math.sin(57.61),
          Inches(90.5) + Inches(19.75) * math.sin(57.61)
        )
      ),
      tolerance = Inches(3),
      position = xyPosition,
      turnPosition = relativeTurn,
      steadyOutput = Percent(50)
    )(drivetrain).withTimeout(Seconds(5)).then(
      toGearAndDrop(drivetrain, gearRoller, gearTilter)
    )
  }
  def rightGearPurePursuit(drivetrain: Drivetrain,
                           gearRoller: GearRoller,
                           gearTilter: GearTilter): FiniteTask = {
    val relativeTurn = drivetrainHardware.turnPosition.relativize((init, curr) => {
      curr - init
    })

    val xyPosition = XYPosition(
      relativeTurn,
      drivetrainHardware.forwardPosition
    )
    new FollowWayPointsWithPosition(
      Seq(
        Point.origin,
        Point(
          Inches(0),
          Inches(90.5)
        ),
        Point(
          - Inches(19.75) * math.sin(57.61),
          Inches(90.5) + Inches(19.75) * math.sin(57.61)
        )
      ),
      tolerance = Inches(3),
      position = xyPosition,
      turnPosition = relativeTurn,
      steadyOutput = Percent(50)
    )(drivetrain).withTimeout(Seconds(5)).then(
      toGearAndDrop(drivetrain, gearRoller, gearTilter)
    )
  }

  val hopperAutoDriveSpeed = Percent(50)
  val hopperTurnThreshold = Degrees(10)

  def hopperForward(drivetrain: Drivetrain): FiniteTask = {
    new DriveDistanceStraight(
      Inches(61.125), // decreased by 8 inches after match 13
      Inches(3),
      hopperTurnThreshold,
      hopperAutoDriveSpeed
    )(drivetrain).withTimeout(Seconds(8))
  }

  def hopperRam(drivetrain: Drivetrain): FiniteTask = {
    new DriveBeyondStraight(
      Inches(39.4), // originally short by 1 ft
      Inches(3),
      hopperTurnThreshold,
      hopperAutoDriveSpeed
    )(drivetrain).withTimeout(Seconds(4)).then(
      new WaitTask(Seconds(0.5)).andUntilDone(
        new DriveOpenLoop(
          drivetrainHardware.forwardPosition.mapToConstant(Percent(40)),
          drivetrainHardware.forwardPosition.mapToConstant(Percent(0))
        )(drivetrain)
      )
    )
  }

  def shootLeftAndDriveBack(drivetrain: Drivetrain,
                            collectorElevator: CollectorElevator,
                            collectorRollers: CollectorRollers,
                            agitator: Agitator,
                            shooterFlywheel: ShooterFlywheel,
                            shooterShifter: ShooterShifter,
                            collectorExtender: CollectorExtender,
                            loadTray: LoadTray): FiniteTask = {
    val shooting = ShooterTasks.continuousShoot(
      shooterFlywheel.midShootSpeedLeft,
      shooterFlywheel.midShootSpeedRight
    )(collectorElevator, collectorRollers, agitator, shooterFlywheel, collectorExtender, loadTray).and(
      new ShiftShooter(shooterFlywheel.midShootSpeedLeft.mapToConstant(ShooterShiftLeft))(shooterShifter)
    )

    new WaitTask(Seconds(5)).andUntilDone(shooting).then(new DriveDistanceStraight(
      Feet(-7),
      Inches(3),
      Degrees(0),
      Percent(30)
    )(drivetrain).withTimeout(Seconds(3)))
  }

  def leftHopperAndShoot(drivetrain: Drivetrain,
                         collectorElevator: CollectorElevator,
                         collectorRollers: CollectorRollers,
                         agitator: Agitator,
                         shooterFlywheel: ShooterFlywheel,
                         shooterShifter: ShooterShifter,
                         collectorExtender: CollectorExtender,
                         loadTray: LoadTray): ContinuousTask = {

    val shooting = ShooterTasks.continuousShoot(
      shooterFlywheel.midShootSpeedLeft,
      shooterFlywheel.midShootSpeedRight
    )(collectorElevator, collectorRollers, agitator, shooterFlywheel, collectorExtender, loadTray).and(
      new ShiftShooter(shooterFlywheel.midShootSpeedLeft.mapToConstant(ShooterShiftLeft))(shooterShifter)
    )

    hopperForward(drivetrain).then(new RotateByAngle(
      Degrees(-90),
      hopperTurnThreshold,
      5
    )(drivetrain).withTimeout(Seconds(3))).then(hopperRam(drivetrain)).andUntilDone(
      new WaitTask(Seconds(6)).then(shooting)
    ).then(shooting)
  }

  def rightHopperAndShoot(drivetrain: Drivetrain,
                          collectorElevator: CollectorElevator,
                          collectorRollers: CollectorRollers,
                          agitator: Agitator,
                          shooterFlywheel: ShooterFlywheel,
                          shooterShifter: ShooterShifter,
                          collectorExtender: CollectorExtender,
                          loadTray: LoadTray): ContinuousTask = {
    val shooting = ShooterTasks.continuousShoot(
      shooterFlywheel.midShootSpeedLeft,
      shooterFlywheel.midShootSpeedRight
    )(collectorElevator, collectorRollers, agitator, shooterFlywheel, collectorExtender, loadTray).and(
      new ShiftShooter(shooterFlywheel.midShootSpeedLeft.mapToConstant(ShooterShiftRight))(shooterShifter)
    )

    hopperForward(drivetrain).then(new RotateByAngle(
      Degrees(90),
      hopperTurnThreshold,
      5
    )(drivetrain).withTimeout(Seconds(3))).then(hopperRam(drivetrain)).andUntilDone(
      new WaitTask(Seconds(6)).then(shooting)
    ).then(shooting)
  }

  def smallTestShot(drivetrain: Drivetrain): ContinuousTask = {
    new DriveDistanceStraight(
      Feet(2), // decreased by 8 inches after match 13
      Inches(3),
      Degrees(10),
      hopperAutoDriveSpeed
    )(drivetrain).withTimeout(Seconds(8)).then(new RotateByAngle(
      Degrees(-90),
      Degrees(10),
      5
    )(drivetrain).withTimeout(Seconds(5))).then(
      new DriveBeyondStraight(
        Feet(2), // originally short by 1 ft
        Inches(3),
        Degrees(10),
        hopperAutoDriveSpeed
      )(drivetrain).withTimeout(Seconds(8))
    ).toContinuous
  }

  def centerGearAndCrossLine(drivetrain: Drivetrain, gearRoller: GearRoller, gearTilter: GearTilter): FiniteTask = {
    val relativeTurn = drivetrainHardware.turnPosition.relativize((init, curr) => {
      curr - init
    })

    val xyPosition = XYPosition(
      relativeTurn,
      drivetrainHardware.forwardPosition
    )


    new DriveDistanceStraight(
      gearPegDistance - robotLength,
      Inches(3),
      Degrees(10),
      Percent(30)
    )(drivetrain).withTimeout(Seconds(8)).then(
      toGearAndDrop(drivetrain, gearRoller, gearTilter)
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
      relativeTurn,
      Percent(50)
    )(drivetrain).withTimeout(Seconds(10)))
  }

  def shootCenterGear(drivetrain: Drivetrain,
                      gearRoller: GearRoller,
                      gearTilter: GearTilter,
                      collectorElevator: CollectorElevator,
                      collectorRollers: CollectorRollers,
                      agitator: Agitator,
                      shooterFlywheel: ShooterFlywheel,
                      collectorExtender: CollectorExtender,
                      loadTray: LoadTray): FiniteTask = {
    new WaitTask(Seconds(3)).andUntilDone(
      ShooterTasks.continuousShoot(
        shooterFlywheel.midShootSpeedLeft,
        shooterFlywheel.midShootSpeedRight
      )(collectorElevator, collectorRollers, agitator, shooterFlywheel, collectorExtender, loadTray)
    ).then(centerGear(drivetrain, gearRoller, gearTilter))
  }
}
