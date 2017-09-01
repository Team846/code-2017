package com.lynbrookrobotics.seventeen.shooter.flywheel

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.drivetrain.MathUtilities
import com.lynbrookrobotics.potassium.{Component, Signal}
import com.lynbrookrobotics.seventeen.driver.DriverHardware
import squants.time.{Milliseconds, Seconds}
import squants.{Each, Percent}
import com.lynbrookrobotics.potassium.streams.Stream


class ShooterFlywheel(implicit properties: Signal[ShooterFlywheelProperties], hardware: ShooterFlywheelHardware, clock: Clock, driverHardware: DriverHardware)
  extends Component[DoubleFlywheelSignal](Milliseconds(5)) {

  val NominalVoltage = 11.9

  override def defaultController = Stream.periodic(Seconds(1)) {
    DoubleFlywheelSignal(Each(0), Each(0))
  }

  /**
    * Compensate for reduced battery voltage
    *
    * @return factor to multiply inputs to imitate behaviour of system where battery voltage
    *         is nominal
    */
  def voltageFactor: Double = {
    val batteryVoltage = driverHardware.station.getBatteryVoltage
    if (batteryVoltage > NominalVoltage / 2) { // reasonable measurement must be than 6 volts
      NominalVoltage / batteryVoltage
    } else {
      // Do not apply correction if voltage is unusually low
      1.0
    }
  }

  override def applySignal(signal: DoubleFlywheelSignal): Unit = {
//    val leftVelocityPercent = Each(hardware.leftVelocity.get / properties.get.maxVelocityLeft)
//    val rightVelocityPercent = Each(hardware.rightVelocity.get / properties.get.maxVelocityRight)
//
//    val leftOut = MathUtilities.limitCurrentOutput(signal.left, leftVelocityPercent, properties.get.currentLimit, properties.get.currentLimit)
//    val rightOut = MathUtilities.limitCurrentOutput(signal.right, rightVelocityPercent, properties.get.currentLimit, properties.get.currentLimit)
//
//    hardware.leftMotor.set(voltageFactor * leftOut.toEach)
//    hardware.rightMotor.set(voltageFactor * rightOut.toEach)

    hardware.leftMotor.set(signal.left.toEach)
    hardware.rightMotor.set(signal.right.toEach)
  }
}
