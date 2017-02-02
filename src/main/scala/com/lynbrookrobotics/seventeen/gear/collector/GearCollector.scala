package com.lynbrookrobotics.seventeen.gear.collector

import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import squants.time.Milliseconds

sealed trait GearCollectorState
case object GearCollectorExtended extends GearCollectorState
case object GearCollectorRetracted extends GearCollectorState

class GearCollector(implicit hardware: GearCollectorHardware) extends Component[GearCollectorState](Milliseconds(5)){
  override def defaultController: PeriodicSignal[GearCollectorState] = Signal.constant(GearCollectorRetracted).toPeriodic

  override def applySignal(signal: GearCollectorState): Unit = {
    hardware.pneumatic.set(signal == GearCollectorExtended)
  }
}
