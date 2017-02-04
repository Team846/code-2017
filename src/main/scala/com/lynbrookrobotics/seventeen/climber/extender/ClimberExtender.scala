package com.lynbrookrobotics.seventeen.climber.extender

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import squants.time.Milliseconds

sealed trait ClimberExtenderState
case object ClimberExtenderExtended extends ClimberExtenderState
case object ClimberExtenderRetracted extends ClimberExtenderState

class ClimberExtender(implicit hardware: ClimberExtenderHardware, clock: Clock) extends Component[ClimberExtenderState](Milliseconds(5)) {
  override def defaultController: PeriodicSignal[ClimberExtenderState] = Signal.constant(ClimberExtenderRetracted).toPeriodic

  override def applySignal(signal: ClimberExtenderState): Unit = {
    hardware.pneumatic.set(signal == ClimberExtenderExtended)
  }
}
