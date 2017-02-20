package com.lynbrookrobotics.seventeen.collector.extender

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import com.lynbrookrobotics.seventeen.gear.tilter.{GearTilter, GearTilterExtended}
import squants.time.Milliseconds

sealed trait CollectorExtenderState
case object CollectorExtenderExtended extends CollectorExtenderState
case object CollectorExtenderRetracted extends CollectorExtenderState

class CollectorExtender(implicit hardware: CollectorExtenderHardware,
                        gearTilter: Option[GearTilter],
                        clock: Clock) extends Component[CollectorExtenderState](Milliseconds(5)) {
  override def defaultController: PeriodicSignal[CollectorExtenderState] = Signal.constant(CollectorExtenderRetracted).toPeriodic

  val gearExtended = gearTilter.map(_.peekedController.map(_.contains(GearTilterExtended))).
    getOrElse(Signal.constant(false))

  override def applySignal(signal: CollectorExtenderState): Unit = {
    if (gearExtended.get) {
      hardware.pneumatic.set(false)
    } else {
      hardware.pneumatic.set(signal == CollectorExtenderExtended)
    }
  }
}
