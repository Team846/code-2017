package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.commons.drivetrain._
import com.lynbrookrobotics.potassium.commons.drivetrain.offloaded.{OffloadedDrive, OffloadedDriveProperties}
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

  override type Properties = OffloadedDriveProperties
  override type Hardware = DrivetrainHardware

  override protected def output(hardware: Hardware, signal: TwoSided[OffloadedSignal]): Unit = {
    if(Math.random()>0.999) {
      println(s"rsig: ${signal.right}")
      println(s"lsig: ${signal.left}")
      println(s"lfee: ${hardware.leftEncoder.getAngularVelocity}")
      println(s"lfee: ${hardware.rightEncoder.getAngularVelocity}")
      println()
    }
    hardware.leftMaster.applyCommand(signal.left)
    hardware.rightMaster.applyCommand(signal.right)
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
