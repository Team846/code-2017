package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.commons.drivetrain._
import com.lynbrookrobotics.potassium.frc.Implicits._
import squants.Each
import squants.time.Milliseconds

package object drivetrain extends TwoSidedDrive(Milliseconds(5)) {
  type Hardware = DrivetrainHardware
  type Properties = DrivetrainProperties


  override protected def output(hardware: DrivetrainHardware,
                                signal: TwoSidedSignal): Unit = {
//    val leftVelocityPercent = Each(hardware.leftVelocity.get / hardware.props.maxLeftVelocity)
//    val rightVelocityPercent = Each(hardware.rightVelocity.get / hardware.props.maxRightVelocity)
//
//    val leftOut = MathUtilities.limitCurrentOutput(signal.left,
//      leftVelocityPercent, hardware.props.currentLimit, hardware.props.currentLimit)
//    val rightOut = MathUtilities.limitCurrentOutput(signal.right,
//      rightVelocityPercent, hardware.props.currentLimit, hardware.props.currentLimit)

//    hardware.leftBack.set(leftOut.toEach)
//    hardware.leftFront.set(leftOut.toEach)
//    hardware.rightBack.set(rightOut.toEach)
//    hardware.rightFront.set(rightOut.toEach)

    hardware.leftBack.set(signal.left.toEach)
    hardware.leftFront.set(signal.left.toEach)
    hardware.rightBack.set(signal.right.toEach)
    hardware.rightFront.set(signal.right.toEach)
  }

  override protected def controlMode(implicit hardware: DrivetrainHardware,
                                     props: DrivetrainProperties): UnicycleControlMode = {
    if (hardware.driverHardware.station.isEnabled && hardware.driverHardware.station.isOperatorControl) {
      ArcadeControlsClosed(
        hardware.driverHardware.joystickStream.map(v => -v.driver.y).map(s =>
          Each(Math.copySign((s * s).toEach, s.toEach))),
        hardware.driverHardware.joystickStream.map(v => -v.driverWheel.x).map(s =>
          Each(Math.copySign((s * s).toEach, s.toEach)))
      )
    } else {
      NoOperation
    }
  }
}
