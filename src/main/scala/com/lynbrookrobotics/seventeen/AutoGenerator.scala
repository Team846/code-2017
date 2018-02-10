package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.control.purePursuit._
import com.lynbrookrobotics.potassium.commons.cartesianPosition.XYPosition
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.streams
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
import squants.{Angle, Percent}
import squants.space.{Degrees, Feet, Inches}
import squants.time.Seconds

class AutoGenerator(r: CoreRobot) {
  import r._

  private val robotLength = Inches(35)

  private val gearPegDistance = Inches(109)

  private val midShootSpeedLeft = r.coreTicks.map(_ => shooterFlywheelProps.get.midShootSpeedLeft)
  private val midShootSpeedRight = r.coreTicks.map(_ => shooterFlywheelProps.get.midShootSpeedRight)

  def printTask(message: String): FiniteTask = {
    new FiniteTask {
      override protected def onEnd(): Unit = {}

      override protected def onStart(): Unit = {
        finished()
        println(message)
      }
    }
  }

  def driveBackPostSwitch(drivetrain: Drivetrain,
                          pose: Stream[Point],
                          relativeAngle: Stream[Angle]): FiniteTask = {
    new FollowWayPointsWithPosition(
      /*Seq(
        Point( // become straight and move 32" forward
          Inches(72.313),
          Inches(110.456)
        ),
        Point(
          Inches(42.464),
          Inches(110.456)
        ),
        Point(
          Inches(42.464),
          Inches(220.300)
        )
      )*/
      postSwitchPoints,
      tolerance = Inches(6),
      maxTurnOutput = Percent(50),
      targetTicksWithingTolerance = 10,
      forwardBackwardMode = BackwardsOnly,
      position = pose,
      turnPosition = relativeAngle
    )(drivetrain)

    /*.then(new FollowWayPointsWithPosition(
      Seq(
        Point(
          Inches(42.464),
          Inches(220.300)
        ),
        Point(
          Inches(55.813),
          Inches(208.688)
        )
      ),
      tolerance = Inches(6),
      maxTurnOutput = Percent(50),
      targetTicksWithingTolerance = 10,
      forwardBackwardMode = ForwardsOnly,
      position = pose,
      turnPosition = drivetrainHardware.turnPosition
    )(drivetrain)).then(new FollowWayPointsWithPosition(
      Seq(
        Point(
          Inches(55.813),
          Inches(208.688)
        ),
        Point(
          Inches(42.464),
          Inches(220.300)
        ),
        Point(
          Inches(0),
          Inches(220.300)
        )
      ),
      tolerance = Inches(6),
      maxTurnOutput = Percent(50),
      targetTicksWithingTolerance = 10,
      forwardBackwardMode = BackwardsOnly,
      position = pose,
      turnPosition = drivetrainHardware.turnPosition
    )(drivetrain)).then(
      new FollowWayPointsWithPosition(
        Seq(
          Point(
            Inches(0),
            Inches(220.300)
          ),
          Point(
            Inches(50.291),
            Inches(299.590),
          )
        ),
        tolerance = Inches(6),
        maxTurnOutput = Percent(50),
        targetTicksWithingTolerance = 10,
        forwardBackwardMode = ForwardsOnly,
        position = pose,
        turnPosition = drivetrainHardware.turnPosition
      )(drivetrain)
    )*/
  }

  def pickupCube(drivetrain: Drivetrain, position: Stream[Point], relativeAngle: Stream[Angle]): FiniteTask = {
    new FollowWayPointsWithPosition(
      cubePickupPoints,
      tolerance = Inches(6),
      maxTurnOutput = Percent(50),
      targetTicksWithingTolerance = 10,
      forwardBackwardMode = ForwardsOnly,
      position = position,
      turnPosition = relativeAngle
    )(drivetrain)
  }

  def driveBackPostCube(drivetrain: Drivetrain, pose: Stream[Point], relativeAngle: Stream[Angle]): FiniteTask = {
    new FollowWayPointsWithPosition(
      driveBackToScalePoints,
      tolerance = Inches(6),
      maxTurnOutput = Percent(50),
      targetTicksWithingTolerance = 10,
      forwardBackwardMode = BackwardsOnly,
      position = pose,
      turnPosition = relativeAngle
    )(drivetrain)
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
    )(drivetrain)
  }

  val startingPose = Point.origin//Point(Inches(139.473), Inches(0))

  def driveDistanceStraight(drivetrain: Drivetrain): FiniteTask = {
    new DriveDistanceStraight(
      Feet(5),
      Inches(2),
      Degrees(5),
      Percent(50)
    )(drivetrain)
  }


  def twoCubeAutoRelative(drivetrain: Drivetrain): FiniteTask = {
    new FollowWayPoints(
      Seq(
        startingPose,
        startingPose + Point(Feet(0), Feet(5))
      ),
      tolerance = Inches(6),
      maxTurnOutput = Percent(50),
      targetTicksWithingTolerance = 10,
      forwardBackwardMode = ForwardsOnly
    )(drivetrain)
  }

  val switchWayPoints = Seq(
    startingPose,
    startingPose + Point(-Feet(2), Feet(6)),
    startingPose + Point(-Feet(2), Feet(10))
  )

  val switchEnd = startingPose + Point(-Feet(2), Feet(10))
  val postSwitchPoints = Seq(
    switchEnd,
    switchEnd + Point(Feet(0), -Feet(4)),
    switchEnd + Point(-Feet(4), -Feet(4)),
    switchEnd + Point(-Feet(4), Feet(1))
  )

  val driveBackEnd = switchEnd + Point(-Feet(4), Feet(1))
  val cubePickupPoints = Seq(
    driveBackEnd,
    driveBackEnd + Point(Feet(2), -Feet(2))
  )

  val cubePickupEnd = driveBackEnd + Point(Feet(2), -Feet(2))
  val driveBackToScalePoints = Seq(
    cubePickupEnd,
    cubePickupEnd + Point(-Feet(2), Feet(1)),
    cubePickupEnd + Point(-Feet(4), Feet(1))
  )

  val driveBackToScaleEnd = cubePickupEnd + Point(-Feet(4), Feet(1))
  val driveToScalePoints = Seq(
    driveBackToScaleEnd,
    driveBackToScaleEnd + Point(Feet(2), Feet(6))

  )

  def twoCubeAuto(drivetrain: Drivetrain): FiniteTask = {
    val relativeTurn = drivetrainHardware.turnPosition.relativize((init, curr) => {
      curr - init
    })

    val xyPosition = XYPosition(
      relativeTurn.map(compassToTrigonometric),
      drivetrainHardware.forwardPosition
    ).map(p =>
      Point(
        p.x + startingPose.x,
        p.y + startingPose.y
      )
    ).preserve

    new FollowWayPointsWithPosition(
//      Seq(
//        startingPose,
//        //        Point( // go forward 12 inches
//        //          Inches(0),
//        //          Inches(30.5)
//        //        ),
//        Point(
//          Inches(72.313),
//          Inches(97.786)
//        ),
//        Point( // become straight and move 32" forward
//          Inches(72.313),
//          Inches(140.188)
//        )
//      ),
      switchWayPoints,
      tolerance = Inches(6),
      maxTurnOutput = Percent(50),
      targetTicksWithingTolerance = 10,
      forwardBackwardMode = ForwardsOnly,
      position = xyPosition,
      turnPosition = relativeTurn
    )(drivetrain).then(printTask("ended switch")).then(
      driveBackPostSwitch(drivetrain, xyPosition, relativeTurn).then(printTask("ended post switch"))
    ).then(
      pickupCube(drivetrain, xyPosition, relativeTurn).then(printTask("end cube pickup"))
    ).then(
      driveBackPostCube(drivetrain, xyPosition, relativeTurn).then(printTask("end back driving"))
    )

/*    new FollowWayPointsWithPosition(
      Seq(
        startingPose,
        //        Point( // go forward 12 inches
        //          Inches(0),
        //          Inches(30.5)
        //        ),
        Point(
          Inches(72.313),
          Inches(97.786)
        ),
        Point( // become straight and move 32" forward
          Inches(72.313),
          Inches(140.188)
        )
      ),
      tolerance = Inches(6),
      maxTurnOutput = Percent(50),
      targetTicksWithingTolerance = 10,
      forwardBackwardMode = ForwardsOnly,
      position = xyPosition,
      turnPosition = drivetrainHardware.turnPosition
    )(drivetrain)*//*.then(
      postSwitchDelivery(drivetrain, xyPosition)
    )*/
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
