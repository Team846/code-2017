package com.lynbrookrobotics.seventeen.gear.tilter

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import com.lynbrookrobotics.seventeen.collector.extender.CollectorExtender
import squants.time.Milliseconds

sealed trait GearTilterState

case object GearTilterExtended extends GearTilterState

case object GearTilterRetracted extends GearTilterState

class GearTilter(implicit hardware: GearTilterHardware,
                 collectorExtenderF: () => Option[CollectorExtender],
                 clock: Clock) extends Component[GearTilterState](Milliseconds(5)) {
  override def defaultController: Stream[GearTilterState] = ??? //Signal.constant(GearTilterRetracted).toPeriodic

  lazy val collectorExtender = collectorExtenderF()

  private var curLastExtendTime: Long = 0
  val lastExtendTime = Signal(curLastExtendTime)

  lazy val collectorExtended = collectorExtender.map(_.lastExtendTime.
    map(t => System.currentTimeMillis() - t <= 1000)).
    getOrElse(Signal.constant(false))

  override def applySignal(signal: GearTilterState): Unit = {
    if (collectorExtended.get) {
      hardware.pneumatic.set(false)
    } else {
      hardware.pneumatic.set(signal == GearTilterExtended)

      if (signal == GearTilterExtended) {
        curLastExtendTime = System.currentTimeMillis()
      }
    }
  }
}
