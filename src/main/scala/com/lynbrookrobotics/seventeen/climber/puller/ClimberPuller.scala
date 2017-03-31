package com.lynbrookrobotics.seventeen.climber.puller

import com.ctre.CANTalon
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.electronics.CurrentLimiting
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import squants.electro.ElectricCurrent
import squants.time.{Milliseconds, Seconds}
import squants.{Dimensionless, Percent}

sealed trait ClimberControlMode

case class CurrentMode(current: ElectricCurrent) extends ClimberControlMode

case class PWMMode(value: Dimensionless) extends ClimberControlMode

class ClimberPuller(implicit hardware: ClimberPullerHardware, clock: Clock) extends Component[ClimberControlMode](Milliseconds(5)) {
  override def defaultController: PeriodicSignal[ClimberControlMode] = Signal.constant(new PWMMode(Percent(0))).toPeriodic

  override def applySignal(signal: ClimberControlMode): Unit = {
    signal match {
      case CurrentMode(current) =>
        hardware.motorA.changeControlMode(CANTalon.TalonControlMode.Current)
        hardware.motorB.changeControlMode(CANTalon.TalonControlMode.Current)
        hardware.motorA.set(current.toAmperes)
        hardware.motorB.set(current.toAmperes)
      case PWMMode(value) =>
        hardware.motorA.changeControlMode(CANTalon.TalonControlMode.PercentVbus)
        hardware.motorB.changeControlMode(CANTalon.TalonControlMode.PercentVbus)
        hardware.motorA.enableBrakeMode(false)
        hardware.motorB.enableBrakeMode(false)
        hardware.motorA.set(value.toEach)
        hardware.motorB.set(value.toEach)
    }
  }

  override def setController(controller: PeriodicSignal[ClimberControlMode]): Unit = {
    super.setController(CurrentLimiting.slewRate(
      controller.map(_.asInstanceOf[PWMMode].value),
      Percent(100) / Seconds(0.3)).map(p => PWMMode(p)))
  }
}
