package com.lynbrookrobotics.seventeen.shooter.flywheel

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, Signal}
import com.lynbrookrobotics.seventeen.driver.DriverHardware
import squants.time.Milliseconds
import squants.Percent


class ShooterFlywheel(implicit hardware: ShooterFlywheelHardware, clock: Clock, driverHardware: DriverHardware)
  extends Component[DoubleFlywheelSignal](Milliseconds(5)) {

  val NominalVoltage = 13.0

  override def defaultController = Signal.constant(
    DoubleFlywheelSignal(Percent(0), Percent(0))
  ).toPeriodic

  /**
    * Compensate for reduced battery voltage
    * @return factor to multiply inputs to imitate behaviour of system where battery voltage
    *         is nominal
    */
  def voltageFactor: Double = {
    val batteryVoltage = driverHardware.station.getBatteryVoltage
    if (batteryVoltage > NominalVoltage / 2) { // reasonable measurement must be than 6 volts
      NominalVoltage / batteryVoltage
    } else {
      NominalVoltage
    }
  }

  override def applySignal(signal: DoubleFlywheelSignal): Unit = {
    hardware.leftMotor.set(/*voltageFactor * */signal.left.toEach)
    hardware.rightMotor.set(/*voltageFactor * */signal.right.toEach)
  }
}
