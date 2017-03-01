package com.lynbrookrobotics.seventeen.collector.extender

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import com.lynbrookrobotics.seventeen.gear.tilter.{GearTilter, GearTilterExtended}
import squants.time.Milliseconds

sealed trait CollectorExtenderState
case object CollectorExtenderExtended extends CollectorExtenderState
case object CollectorExtenderRetracted extends CollectorExtenderState

class CollectorExtender(implicit hardware: CollectorExtenderHardware,
                        gearTilterF: () => Option[GearTilter],
                        clock: Clock) extends Component[CollectorExtenderState](Milliseconds(5)) {
  override def defaultController: PeriodicSignal[CollectorExtenderState] = Signal.constant(CollectorExtenderRetracted).toPeriodic

  lazy val gearTilter = gearTilterF()

  private var curLastExtendTime: Long = 0
  val lastExtendTime = Signal(curLastExtendTime)

  lazy val gearExtended = gearTilter.map(_.lastExtendTime.
    map(t => System.currentTimeMillis() - t <= 1000)).
    getOrElse(Signal.constant(false))

  override def applySignal(signal: CollectorExtenderState): Unit = {
    if (gearExtended.get) {
      hardware.pneumatic.set(false)
    } else {
      hardware.pneumatic.set(signal == CollectorExtenderExtended)

      if (signal == CollectorExtenderExtended) {
        curLastExtendTime = System.currentTimeMillis()
      }
    }
  }
}
