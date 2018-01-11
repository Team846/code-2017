package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.commons.drivetrain._
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided._
import com.lynbrookrobotics.potassium.commons.electronics.CurrentLimiting
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.{Component, Signal}
import squants.Each
import squants.electro.Volts
import squants.space.Degrees

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


  override def velocityControl(tankSpeed: Stream[TwoSidedVelocity])
                              (implicit hardware: DrivetrainHardware,
                               props: Signal[DrivetrainProperties]): Stream[TwoSidedSignal] = {
    val angles: Stream[squants.Angle] = hardware.driverHardware.joystickStream.map(values => Degrees(values.driverWheel
      .x *
      90))
    val velocity = hardware.driverHardware.joystickStream.map(-_.driver.y.toEach * props.get.maxForwardVelocity)
//    velocity.foreach(println)
    val busValues = BusDriving.getTurnRadius(angles)
    super.velocityControl(BlendedDriving.driveWithRadius(busValues, velocity))
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


    override def defaultController: Stream[TwoSidedSignal] = self.defaultController

    val normalDrivetrainVoltage = Volts(12)

    override def applySignal(signal: TwoSidedSignal): Unit = {
      val currentVoltage = Volts(hardware.driverHardware.station.getBatteryVoltage)
      val compFactor = normalDrivetrainVoltage / currentVoltage
      output(
        hardware,
        TwoSidedSignal(signal.left * compFactor, signal.right * compFactor))
    }
  }
}
