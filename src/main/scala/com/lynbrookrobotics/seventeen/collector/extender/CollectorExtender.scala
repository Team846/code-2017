package com.lynbrookrobotics.seventeen.collector.extender

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import squants.time.Milliseconds

sealed trait CollectorExtenderState
case object CollectorExtenderExtended extends CollectorExtenderState
case object CollectorExtenderRetracted extends CollectorExtenderState

class CollectorExtender(implicit hardware: CollectorExtenderHardware, clock: Clock) extends Component[CollectorExtenderState](Milliseconds(5)) {
  override def defaultController: PeriodicSignal[CollectorExtenderState] = Signal.constant(CollectorExtenderRetracted).toPeriodic


  override def applySignal(signal: CollectorExtenderState): Unit = {
    hardware.pneumatic.set(signal == CollectorExtenderExtended)
  }
}
