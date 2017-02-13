package com.lynbrookrobotics.seventeen.gear.tilter

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import com.lynbrookrobotics.seventeen.collector.extender.{CollectorExtender, CollectorExtenderExtended}
import squants.time.Milliseconds

sealed trait GearTilterState
case object GearTilterExtended extends GearTilterState
case object GearTilterRetracted extends GearTilterState

class GearTilter(implicit hardware: GearTilterHardware,
                 collectorExtender: CollectorExtender,
                 clock: Clock) extends Component[GearTilterState](Milliseconds(5)){
  override def defaultController: PeriodicSignal[GearTilterState] = Signal.constant(GearTilterRetracted).toPeriodic

  val collectorExtended = collectorExtender.peekedController.map(_.contains(CollectorExtenderExtended))

  override def applySignal(signal: GearTilterState): Unit = {
    if (collectorExtended.get) {
      hardware.pneumatic.set(false)
    } else {
      hardware.pneumatic.set(signal == GearTilterExtended)
    }
  }
}
