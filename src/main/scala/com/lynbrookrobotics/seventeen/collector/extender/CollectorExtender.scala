package com.lynbrookrobotics.seventeen.collector.extender

import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.{Component, Signal}
import com.lynbrookrobotics.seventeen.gear.tilter.GearTilter
import squants.time.Milliseconds

sealed trait CollectorExtenderState

case object CollectorExtenderExtended extends CollectorExtenderState

case object CollectorExtenderRetracted extends CollectorExtenderState

class CollectorExtender(val coreTicks: Stream[Unit], gearTilter: => Option[GearTilter])
                       (implicit hardware: CollectorExtenderHardware) extends Component[CollectorExtenderState](Milliseconds(5)) {
  override def defaultController: Stream[CollectorExtenderState] = coreTicks.mapToConstant(CollectorExtenderRetracted)

  private var curLastExtendTime: Long = 0
  val lastExtendTime = Signal(curLastExtendTime)

  lazy val gearExtended = gearTilter.map(_.lastExtendTime.
    map(t => System.currentTimeMillis() - t <= 1000)).
    getOrElse(Signal.constant(false))

  lazy val somethingExtended = gearExtended

  override def applySignal(signal: CollectorExtenderState): Unit = {
    if (somethingExtended.get) {
      hardware.pneumatic.set(false)
    } else {
      hardware.pneumatic.set(signal == CollectorExtenderExtended)

      if (signal == CollectorExtenderExtended) {
        curLastExtendTime = System.currentTimeMillis()
      }
    }
  }
}
