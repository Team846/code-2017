package com.lynbrookrobotics.seventeen.agitator

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.electronics.CurrentLimiting
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import squants.{Dimensionless, Percent}
import squants.time.{Milliseconds, Seconds}

class Agitator(implicit hardware: AgitatorHardware, clock: Clock) extends Component[Dimensionless](Milliseconds(5)){
  override def defaultController: PeriodicSignal[Dimensionless] = Signal.constant(Percent(0)).toPeriodic

  var first = true
  override def setController(controller: PeriodicSignal[Dimensionless]): Unit = {
    super.setController(CurrentLimiting.slewRate(controller, Percent(100) / Seconds(0.3)).withCheck{_ =>
      if (first) {
        Thread.currentThread().setName("Agitator-thread")
        Thread.currentThread().setPriority(10)
        first = false
      }
    })
  }

  override def applySignal(signal: Dimensionless): Unit = {
    hardware.motor.set(signal.toEach)
  }
}
