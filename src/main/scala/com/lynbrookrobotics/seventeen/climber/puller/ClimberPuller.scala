package com.lynbrookrobotics.seventeen.climber.puller

import com.ctre.phoenix.motorcontrol.{ControlMode, NeutralMode}
import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.commons.electronics.CurrentLimiting
import com.lynbrookrobotics.potassium.streams.Stream
import squants.electro.ElectricCurrent
import squants.time.{Milliseconds, Seconds}
import squants.{Dimensionless, Percent}

sealed trait ClimberControlMode

case class CurrentMode(current: ElectricCurrent) extends ClimberControlMode

case class PWMMode(value: Dimensionless) extends ClimberControlMode

class ClimberPuller(val coreTicks: Stream[Unit])(implicit hardware: ClimberPullerHardware) extends Component[ClimberControlMode] {
  override def defaultController: Stream[ClimberControlMode] = coreTicks.mapToConstant(PWMMode(Percent(0)))

  override def applySignal(signal: ClimberControlMode): Unit = {
    signal match {
      case CurrentMode(current) =>
        hardware.motorA.set(ControlMode.Current, current.toAmperes)
        hardware.motorB.set(ControlMode.Current, current.toAmperes)
      case PWMMode(value) =>
        hardware.motorA.setNeutralMode(NeutralMode.Brake)
        hardware.motorB.setNeutralMode(NeutralMode.Brake)
        hardware.motorA.set(ControlMode.PercentOutput, value.toEach)
        hardware.motorB.set(ControlMode.PercentOutput, value.toEach)
    }
  }

  override def setController(controller: Stream[ClimberControlMode]): Unit = {
    super.setController(CurrentLimiting.slewRate(
      Percent(0),
      controller.map(_.asInstanceOf[PWMMode].value),
      Percent(100) / Seconds(0.3)
    ).map(p => PWMMode(p)))
  }
}
