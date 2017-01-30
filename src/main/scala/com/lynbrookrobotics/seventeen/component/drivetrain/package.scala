package com.lynbrookrobotics.seventeen.component

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.drivetrain._
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.seventeen.hardware
import com.lynbrookrobotics.seventeen.config
import com.lynbrookrobotics.potassium.frc.Implicits._
import squants.Time
import squants.time.Milliseconds

package object drivetrain extends TwoSidedDrive {
  type DrivetrainHardware = hardware.DrivetrainHardware
  type DrivetrainProperties = config.DrivetrainProperties

  override protected implicit val clock: Clock = WPIClock
  override protected val updatePeriod: Time = Milliseconds(5)

  override protected def output(hardware: DrivetrainHardware, signal: TwoSidedSignal): Unit = {
    hardware.leftBack.set(signal.left.toEach)
    hardware.leftFront.set(signal.left.toEach)
    hardware.rightBack.set(signal.right.toEach)
    hardware.rightFront.set(signal.right.toEach)
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
