package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.commons.drivetrain._
import com.lynbrookrobotics.potassium.commons.drivetrain.offloaded.{OffloadedDrive, OffloadedProperties}
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided._
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.{Component, Signal}
import squants.Each

package object drivetrain extends OffloadedDrive {
  self =>
  class Drivetrain(implicit hardware: Hardware,
                   props: Signal[Properties]) extends Component[DriveSignal] {

    override def defaultController: Stream[DriveSignal] = self.defaultController

    override def applySignal(signal: TwoSided[OffloadedSignal]): Unit = {
      output(
        hardware,
        signal)
    }
  }

  override type Properties = OffloadedProperties
  override type Hardware = DrivetrainHardware

  override protected def output(hardware: Hardware, signal: TwoSided[OffloadedSignal]): Unit = {
    hardware.leftFront.applyCommand(signal.left)
    hardware.leftBack.applyCommand(signal.left)
    hardware.rightBack.applyCommand(signal.right) // TODO: Make right side negative
    hardware.rightBack.applyCommand(signal.right)
  }

  override protected def controlMode(implicit hardware: Hardware, props: Properties): UnicycleControlMode = {
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
}
