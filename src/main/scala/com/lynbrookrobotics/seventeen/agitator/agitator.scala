package com.lynbrookrobotics.seventeen.agitator

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import squants.time.Milliseconds

sealed trait AgitatorState
case object AgitatorSpinning extends AgitatorState
case object AgitatorStopped extends AgitatorState

class Agitator(implicit hardware: AgitatorHardware, clock: Clock, properties: AgitatorProperties) extends Component[AgitatorState](Milliseconds(5)){
  override def defaultController: PeriodicSignal[AgitatorState] = Signal.constant(AgitatorStopped).toPeriodic

  override def applySignal(signal: AgitatorState): Unit = {
    if(signal==AgitatorSpinning) {
      hardware.motor.set(properties.talonSpeed)
    } else {
      hardware.motor.set(0)

    }
  }
}
