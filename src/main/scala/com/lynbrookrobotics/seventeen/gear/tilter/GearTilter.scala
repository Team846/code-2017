package com.lynbrookrobotics.seventeen.gear.tilter

import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.{Component, Signal}
import com.lynbrookrobotics.seventeen.collector.extender.CollectorExtender
import squants.time.Milliseconds

sealed trait GearTilterState

case object GearTilterExtended extends GearTilterState

case object GearTilterRetracted extends GearTilterState

class GearTilter(val coreTicks: Stream[Unit],
                 collectorExtender: => Option[CollectorExtender])
                (implicit hardware: GearTilterHardware) extends Component[GearTilterState](Milliseconds(5)) {
  override def defaultController: Stream[GearTilterState] = coreTicks.mapToConstant(GearTilterRetracted)

  private var curLastExtendTime: Long = 0
  private var curLastRetractTime: Long = 0
  val lastExtendTime = Signal(curLastExtendTime)
  val lastRetractTime = Signal(curLastRetractTime)

  lazy val collectorExtended = collectorExtender.map(_.lastExtendTime.
    map(t => System.currentTimeMillis() - t <= 1000)).
    getOrElse(Signal.constant(false))

  private var curLastOpenTime: Long = 0
  val lastOpenTime = Signal(curLastOpenTime)

  override def applySignal(signal: GearTilterState): Unit = {
    val outWithSafety = if (collectorExtended.get) {
      GearTilterRetracted
    } else {
      signal
    }

    hardware.pneumatic.set(outWithSafety == GearTilterExtended)

    if (outWithSafety == GearTilterExtended) {
      curLastExtendTime = System.currentTimeMillis()
    } else {
      curLastRetractTime = System.currentTimeMillis()
    }
  }
}
