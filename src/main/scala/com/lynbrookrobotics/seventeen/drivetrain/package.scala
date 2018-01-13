package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.commons.drivetrain._
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.{TwoSidedDrive, TwoSidedSignal, TwoSidedVelocity}
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.UnicycleSignal
import com.lynbrookrobotics.potassium.commons.electronics.CurrentLimiting
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.{Component, Signal}
import squants.electro.Volts
import squants.{Each, Percent}

package object drivetrain extends TwoSidedDrive { self =>
  type Hardware = DrivetrainHardware
  type Properties = DrivetrainProperties


  override protected def output(hardware: DrivetrainHardware,
                                signal: TwoSidedSignal): Unit = {
    hardware.leftBack.set(signal.left.toEach)
    hardware.leftFront.set(signal.left.toEach)
    hardware.rightBack.set(-signal.right.toEach)
    hardware.rightFront.set(-signal.right.toEach)
  }

  override protected def controlMode(implicit hardware: DrivetrainHardware,
                                     props: DrivetrainProperties): UnicycleControlMode = {
    if (hardware.driverHardware.station.isEnabled && hardware.driverHardware.station.isOperatorControl) {
      ArcadeControlsClosed(
        hardware.driverHardware.joystickStream.map(v => -v.driver.y).map(s =>
          Each(Math.copySign((s * s).toEach, s.toEach))),
        hardware.driverHardware.joystickStream.map(v => v.driverWheel.x).map(s =>
          Each(Math.copySign((s * s).toEach, s.toEach)))
      )
    } else {
      NoOperation
    }
  }


  override def velocityControl(target: Stream[TwoSidedVelocity])
                              (implicit hardware: DrivetrainHardware,
                               props: Signal[DrivetrainProperties]): Stream[TwoSidedSignal] = {
    super.velocityControl(target)
  }

  class Drivetrain(implicit hardware: Hardware,
                   props: Signal[Properties]) extends Component[TwoSidedSignal] {
    override def setController(controller: Stream[TwoSidedSignal]): Unit = {
      val currentLimited = controller.zip(hardware.leftVelocity).zip(hardware.rightVelocity).map { case ((control, leftVelocity), rightVelocity) =>
        val leftVelocityPercent = Each(leftVelocity / hardware.props.maxLeftVelocity)
        val rightVelocityPercent = Each(rightVelocity / hardware.props.maxRightVelocity)

        val leftOut = CurrentLimiting.limitCurrentOutput(control.left,
          leftVelocityPercent, hardware.props.currentLimit, hardware.props.currentLimit)
        val rightOut = CurrentLimiting.limitCurrentOutput(control.right,
          rightVelocityPercent, hardware.props.currentLimit, hardware.props.currentLimit)

        TwoSidedSignal(leftOut, rightOut)
      }

      super.setController(currentLimited)
    }



    override def defaultController: Stream[TwoSidedSignal] = {
      controlMode(hardware, props.get) match {
        case NoOperation =>
          UnicycleControllers.parentOpenLoop(hardware.forwardPosition.mapToConstant(UnicycleSignal(Percent(0), Percent(0))))

        case ArcadeControlsOpen(forward, turn) =>
          val combinedSignal = forward.zip(turn).map(t => UnicycleSignal(t._1, t._2))
          UnicycleControllers.parentOpenLoop(combinedSignal)

        case ArcadeControlsClosed(forward, turn) =>
          val combinedSignal = forward.zip(turn).map(t => UnicycleSignal(t._1, t._2))
          val curvature = hardware.driverHardware.joystickStream.map {
            props.get.maxCurvature * _.driverWheel.x.toEach
          }

          blendedVelocityControl(combinedSignal, curvature)
      }
    }

    val normalDrivetrainVoltage = Volts(12)

    override def applySignal(signal: TwoSidedSignal): Unit = {
      val compFactor = normalDrivetrainVoltage / Volts(hardware.driverHardware.station.getBatteryVoltage)
      output(hardware, TwoSidedSignal(signal.left * compFactor, signal.right * compFactor))
    }
  }
}
