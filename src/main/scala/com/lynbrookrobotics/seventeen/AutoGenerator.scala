package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.control.purePursuit._
import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.tasks.{ContinuousTask, FiniteTask, WaitTask}
import com.lynbrookrobotics.potassium.units.Point
import com.lynbrookrobotics.seventeen.agitator.Agitator
import com.lynbrookrobotics.seventeen.collector.elevator.CollectorElevator
import com.lynbrookrobotics.seventeen.collector.extender.CollectorExtender
import com.lynbrookrobotics.seventeen.collector.rollers.CollectorRollers
import com.lynbrookrobotics.seventeen.drivetrain.Drivetrain
import com.lynbrookrobotics.seventeen.drivetrain.unicycleTasks._
import com.lynbrookrobotics.seventeen.gear.grabber.{GearGrabber, OpenGrabber}
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

  private val midShootSpeedLeft = r.coreTicks.map(_ => shooterFlywheelProps.get.midShootSpeedLeft)
  private val midShootSpeedRight = r.coreTicks.map(_ => shooterFlywheelProps.get.midShootSpeedRight)


  def postSwitchDelivery(drivetrain: Drivetrain): FiniteTask = {
    new FollowWayPoints(
      Seq(
        Point.origin,
        Point(
          Inches(0),
          Inches(-18)
        ),
        Point(
          Inches(-30),
          Inches(-18)
        ),
        Point(
          Inches(-30),
          Inches(82)
        )
      ),
      tolerance = Inches(6),
      maxTurnOutput = Percent(50),
      targetTicksWithingTolerance = 10,
      forwardBackwardMode = BackwardsOnly
    )(drivetrain).then(new FollowWayPoints(
      Seq(
        Point.origin,
        Point(
          Inches(20.553),
          Inches(-14.165)
        )
      ),
      tolerance = Inches(6),
      maxTurnOutput = Percent(50),
      targetTicksWithingTolerance = 10,
      forwardBackwardMode = ForwardsOnly
    )(drivetrain)).then(new RotateByAngle(
      Degrees(180),
      Degrees(30),
      1
    )(drivetrain)).then(new FollowWayPoints(
      Seq(
        Point.origin,
        Point(
          Inches(-9.235),
          Inches(91.119)
        )
      ),
      tolerance = Inches(6),
      maxTurnOutput = Percent(50),
      targetTicksWithingTolerance = 10,
      forwardBackwardMode = ForwardsOnly
    )(drivetrain))
  }

  def centerSwitch(drivetrain: Drivetrain): FiniteTask = {
    new FollowWayPoints(
      Seq(
        Point.origin,
        Point(
          Inches(-55.393),
          Inches(111.993)
        ),
        Point(
          Inches(-55.393),
          Inches(143.993)
        )
      ),
      tolerance = Inches(6),
      maxTurnOutput = Percent(50),
      targetTicksWithingTolerance = 10,
      forwardBackwardMode = ForwardsOnly
    )(drivetrain).then(postSwitchDelivery(drivetrain))
  }

  def twoCubeAuto(drivetrain: Drivetrain): FiniteTask = {
    val relativeTurn = drivetrainHardware.turnPosition.relativize((init, curr) => {
      curr - init
    })

    val xyPosition = XYPosition(
      relativeTurn,
      drivetrainHardware.forwardPosition
    )

    new FollowWayPoints(
      Seq(
        Point.origin,
        //        Point( // go forward 12 inches
        //          Inches(0),
        //          Inches(6.6)
        //        ),
        Point( // turn 45 degrees counterclockwise and move 65.1" forward
          Inches(-55.393),
          Inches(30)
        ),
        Point( // become straight and move 32" forward
          Inches(-55.393),
          Inches(40)
        )
      ),
      tolerance = Inches(6),
      maxTurnOutput = Percent(50),
      targetTicksWithingTolerance = 10,
      forwardBackwardMode = ForwardsOnly
    )(drivetrain).then(
      postSwitchDelivery(drivetrain)
    )
  }

  def sameSideScaleAuto(drivetrain: Drivetrain): FiniteTask = {
    new FollowWayPoints(
      Seq(
        Point.origin,
        Point( // turn 45 degrees counterclockwise and move 65.1" forward
          Inches(0),
          Inches(200) - Feet(5)
        ),
        Point(
          Inches(50) - Inches(20),
          Inches(200)
        ),
        Point( // turn 45 degrees clockwise and move 32" forward
          Inches(50),
          Inches(200)
        )
      ),
      tolerance = Inches(6),
      maxTurnOutput = Percent(50),
      targetTicksWithingTolerance = 10,
      forwardBackwardMode = ForwardsOnly
    )(drivetrain)
  }
//  def leftGearPurePursuit(drivetrain: Drivetrain,
//                          gearGrabber: GearGrabber,
//                          gearTilter: GearTilter): FiniteTask = {
//    val relativeTurn = drivetrainHardware.turnPosition.relativize((init, curr) => {
//      curr - init
//    })
//
//    val xyPosition = XYPosition(
//      relativeTurn,
//      drivetrainHardware.forwardPosition
//    )
//    new FollowWayPointsWithPosition(
//      Seq(
//        Point.origin,
//        Point(
//          Inches(0),
//          Inches(90.5)
//        ),
//        Point(
//          Inches(19.75) * math.sin(57.61),
//          Inches(90.5) + Inches(19.75) * math.sin(57.61)
//        )
//      ),
//      tolerance = Inches(3),
//      position = xyPosition,
//      turnPosition = relativeTurn,

//      maxTurnOutput = Percent(50)
//    )(drivetrain).withTimeout(Seconds(5)).then(
//      toGearAndDrop(drivetrain, gearGrabber, gearTilter)
//    )
//  }
//  def rightGearPurePursuit(drivetrain: Drivetrain,
//                          gearGrabber: GearGrabber,
//                          gearTilter: GearTilter): FiniteTask = {
//    val relativeTurn = drivetrainHardware.turnPosition.relativize((init, curr) => {
//      curr - init
//    })
//
//    val xyPosition = XYPosition(
//      relativeTurn,
//      drivetrainHardware.forwardPosition
//    )
//    new FollowWayPointsWithPosition(
//      Seq(
//        Point.origin,
//        Point(
//          Inches(0),
//          Inches(90.5)
//        ),
//        Point(
//          - Inches(19.75) * math.sin(57.61),
//          Inches(90.5) + Inches(19.75) * math.sin(57.61)
//        )
//      ),
//      tolerance = Inches(3),
//      position = xyPosition,
//      turnPosition = relativeTurn,
//      maxTurnOutput = Percent(50)
//    )(drivetrain).withTimeout(Seconds(5)).then(
//      toGearAndDrop(drivetrain, gearGrabber, gearTilter)
//    )
//  }
//
//  val hopperAutoDriveSpeed = Percent(50)
//  val hopperTurnThreshold = Degrees(10)
//
//  def hopperForward(drivetrain: Drivetrain): FiniteTask = {
//    new DriveDistanceStraight(
//      Inches(61.125), // decreased by 8 inches after match 13
//      Inches(3),
//      hopperTurnThreshold,
//      hopperAutoDriveSpeed
//    )(drivetrain).withTimeout(Seconds(8))
//  }
//
//  def hopperRam(drivetrain: Drivetrain): FiniteTask = {
//    new DriveBeyondStraight(
//      Inches(39.4), // originally short by 1 ft
//      Inches(3),
//      hopperTurnThreshold,
//      hopperAutoDriveSpeed
//    )(drivetrain).withTimeout(Seconds(4)).then(
//      new WaitTask(Seconds(0.5)).andUntilDone(
//        new DriveOpenLoop(
//          drivetrainHardware.forwardPosition.mapToConstant(Percent(40)),
//          drivetrainHardware.forwardPosition.mapToConstant(Percent(0))
//        )(drivetrain)
//      )
//    )
//  }
//
//  def shootLeftAndDriveBack(drivetrain: Drivetrain,
//                            collectorElevator: CollectorElevator,
//                            collectorRollers: CollectorRollers,
//                            agitator: Agitator,
//                            shooterFlywheel: ShooterFlywheel,
//                            shooterShifter: ShooterShifter,
//                            collectorExtender: CollectorExtender,
//                            loadTray: LoadTray): FiniteTask = {
//    val shooting = ShooterTasks.continuousShoot(
//      midShootSpeedLeft,
//      midShootSpeedRight
//    )(collectorElevator, collectorRollers, agitator, shooterFlywheel, collectorExtender, loadTray).and(
//      new ShiftShooter(midShootSpeedLeft.mapToConstant(ShooterShiftLeft))(shooterShifter)
//    )
//
//    new WaitTask(Seconds(5)).andUntilDone(shooting).then(new DriveDistanceStraight(
//      Feet(-7),
//      Inches(3),
//      Degrees(0),
//      Percent(30)
//    )(drivetrain).withTimeout(Seconds(3)))
//  }
//  def leftHopperAndShoot(drivetrain: Drivetrain,
//                         collectorElevator: CollectorElevator,
//                         collectorRollers: CollectorRollers,
//                         agitator: Agitator,
//                         shooterFlywheel: ShooterFlywheel,
//                         shooterShifter: ShooterShifter,
//                         collectorExtender: CollectorExtender,
//                         loadTray: LoadTray): ContinuousTask = {
//
//    val shooting = ShooterTasks.continuousShoot(
//      midShootSpeedLeft,
//      midShootSpeedRight
//    )(collectorElevator, collectorRollers, agitator, shooterFlywheel, collectorExtender, loadTray).and(
//      new ShiftShooter(midShootSpeedLeft.mapToConstant(ShooterShiftLeft))(shooterShifter)
//    )
//
//    hopperForward(drivetrain).then(new RotateByAngle(
//      Degrees(-90),
//      hopperTurnThreshold,
//      5
//    )(drivetrain).withTimeout(Seconds(3))).then(hopperRam(drivetrain)).andUntilDone(
//      new WaitTask(Seconds(6)).then(shooting)
//    ).then(shooting)
//  }
//
//  def rightHopperAndShoot(drivetrain: Drivetrain,
//                          collectorElevator: CollectorElevator,
//                          collectorRollers: CollectorRollers,
//                          agitator: Agitator,
//                          shooterFlywheel: ShooterFlywheel,
//                          shooterShifter: ShooterShifter,
//                          collectorExtender: CollectorExtender,
//                          loadTray: LoadTray): ContinuousTask = {
//    val shooting = ShooterTasks.continuousShoot(
//      midShootSpeedLeft,
//      midShootSpeedRight
//    )(collectorElevator, collectorRollers, agitator, shooterFlywheel, collectorExtender, loadTray).and(
//      new ShiftShooter(midShootSpeedLeft.mapToConstant(ShooterShiftRight))(shooterShifter)
//    )
//
//    hopperForward(drivetrain).then(new RotateByAngle(
//      Degrees(90),
//      hopperTurnThreshold,
//      5
//    )(drivetrain).withTimeout(Seconds(3))).then(hopperRam(drivetrain)).andUntilDone(
//      new WaitTask(Seconds(6)).then(shooting)
//    ).then(shooting)
//  }
//
//  def smallTestShot(drivetrain: Drivetrain): ContinuousTask = {
//    new DriveDistanceStraight(
//      Feet(2), // decreased by 8 inches after match 13
//      Inches(3),
//      Degrees(10),
//      hopperAutoDriveSpeed
//    )(drivetrain).withTimeout(Seconds(8)).then(new RotateByAngle(
//      Degrees(-90),
//      Degrees(10),
//      5
//    )(drivetrain).withTimeout(Seconds(5))).then(
//      new DriveBeyondStraight(
//        Feet(2), // originally short by 1 ft
//        Inches(3),
//        Degrees(10),
//        hopperAutoDriveSpeed
//      )(drivetrain).withTimeout(Seconds(8))
//    ).toContinuous
//  }
//
//  def printTask(message: String): FiniteTask = new FiniteTask {
//    override protected def onEnd(): Unit = {
//      println("end of print task")
//    }
//
//    override protected def onStart(): Unit = {
//      println(message)
//      finished()
//    }
//  }
//
//  def centerDriveBack(drivetrain: Drivetrain): FiniteTask = {
//
//    val arcTurning = new DriveBeyondStraight(
//      Inches(18),
//      Inches(1),
//      Degrees(0),
//      Percent(50)
//    ).then(
//      new Dri
//    )
//
//    val driveTask = new FollowWayPoints(
//      Seq(
//        Point.origin,
//        Point(Feet(0), Feet(-1)),
//        Point(Feet(-3), Feet(-1)),
//        Point(Feet(-3), Feet(5))
//      ),
//      tolerance = Inches(6),
//      maxTurnOutput = Percent(50),
//      targetTicksWithingTolerance = 20,
//      forwardBackwardMode = BackwardsOnly
//    )(drivetrain)/*.then(
//      new FollowWayPoints(
//        Seq(
//          Point.origin,
//          Point(Feet(0), Feet(-1)),
//          Point(Feet(-3), Feet(-1)),
//          Point(Feet(-3), Feet(5))
//        ),
//        tolerance = Inches(6),
//        maxTurnOutput = Percent(50),
//        targetTicksWithingTolerance = 20,
//        forwardBackwardMode = BackwardsOnly
//    )*/
//
//    /*printTask("starting centerDriveBack").then*/(
//      driveTask
//    )/*.then(
//      printTask("ending centerDriveBack")
//    )*/
//  }
//
//    def driveForwardOpenLoop(drivetrain: Drivetrain, updateSource: Stream[_]): ContinuousTask = {
//      new DriveOpenLoop(
//        updateSource.mapToConstant(Percent(60)),
//        updateSource.mapToConstant(Percent(0)),
//        "forward open loop"
//      )(drivetrain)
//    }
//
//    def driveForwardOpenLoop5seconds(drivetrain: Drivetrain, updateSource: Stream[_]): ContinuousTask = {
//      driveForwardOpenLoop(drivetrain, updateSource).forDuration(Seconds(5)).toContinuous
//    }
//
//
//
//    def centerGearAndCrossLine(drivetrain: Drivetrain, gearGrabber: GearGrabber, gearTilter: GearTilter): FiniteTask = {
//    val relativeTurn = drivetrainHardware.turnPosition.relativize((init, curr) => {
//      curr - init
//    })
//
//    val xyPosition = XYPosition(
//      relativeTurn,
//      drivetrainHardware.forwardPosition
//    )
//
//
//    new DriveDistanceStraight(
//      gearPegDistance - robotLength,
//      Inches(3),
//      Degrees(10),
//      Percent(30)
//    )(drivetrain).withTimeout(Seconds(8)).then(
//      toGearAndDrop(drivetrain, gearGrabber, gearTilter)
//    ).then(new FollowWayPointsWithPosition(
//      Seq(
//        new Point(
//          Inches(0),
//          gearPegDistance - robotLength
//        ),
//        new Point(
//          Inches(0),
//          gearPegDistance - robotLength - Feet(3)
//        ),
//        new Point(
//          Feet(4),
//          gearPegDistance - robotLength - Feet(4)
//        ),
//        new Point(
//          Feet(8),
//          gearPegDistance - robotLength - Feet(3)
//        ),
//        new Point(
//          Feet(8),
//          gearPegDistance - robotLength + Feet(9)
//        )
//      ),
//      Feet(0),
//      xyPosition,
//      relativeTurn,
//      maxTurnOutput = Percent(50)
//    )(drivetrain).withTimeout(Seconds(10)))
//  }
//
//  def shootCenterGear(drivetrain: Drivetrain,
//                      gearGrabber: GearGrabber,
//                      gearTilter: GearTilter,
//                      collectorElevator: CollectorElevator,
//                      collectorRollers: CollectorRollers,
//                      agitator: Agitator,
//                      shooterFlywheel: ShooterFlywheel,
//                      collectorExtender: CollectorExtender,
//                      loadTray: LoadTray): FiniteTask = {
//    new WaitTask(Seconds(3)).andUntilDone(
//      ShooterTasks.continuousShoot(
//        midShootSpeedLeft,
//        midShootSpeedRight
//      )(collectorElevator, collectorRollers, agitator, shooterFlywheel, collectorExtender, loadTray)
//    ).then(centerGear(drivetrain, gearGrabber, gearTilter))
//  }
//}
}
