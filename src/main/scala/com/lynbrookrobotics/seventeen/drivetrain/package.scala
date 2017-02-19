package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.drivetrain.{ArcadeControlsClosed, NoOperation, TwoSidedDrive, UnicycleControlMode}
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.frc.Implicits._
import squants.Time
import squants.time.Milliseconds

package object drivetrain extends TwoSidedDrive(Milliseconds(5)) {
  type Hardware = DrivetrainHardware
  type Properties = DrivetrainProperties

  override protected def output(hardware: DrivetrainHardware, signal: TwoSidedSignal): Unit = {
    hardware.leftBack.set(signal.left.toEach min 0.7)
    hardware.leftFront.set(signal.left.toEach min 0.7)
    hardware.rightBack.set(signal.right.toEach min 0.7)
    hardware.rightFront.set(signal.right.toEach min 0.7)
  }

  override protected def controlMode(implicit hardware: DrivetrainHardware,
                                     props: DrivetrainProperties): UnicycleControlMode = {
    if (hardware.driverHardware.station.isEnabled && hardware.driverHardware.station.isOperatorControl) {
      ArcadeControlsClosed(
        hardware.driverHardware.driverJoystick.y.map(-_),
        hardware.driverHardware.driverWheel.x
      )
    } else {
      NoOperation
    }
  }
}
