package com.lynbrookrobotics.seventeen.collector.extender

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import com.lynbrookrobotics.seventeen.gear.tilter.GearTilter
import com.lynbrookrobotics.seventeen.loadtray.LoadTray
import squants.time.Milliseconds

sealed trait CollectorExtenderState

case object CollectorExtenderExtended extends CollectorExtenderState

case object CollectorExtenderRetracted extends CollectorExtenderState

class CollectorExtender(implicit hardware: CollectorExtenderHardware,
                        gearTilterF: () => Option[GearTilter],
                        loadTrayF: () => Option[LoadTray],
                        clock: Clock) extends Component[CollectorExtenderState](Milliseconds(5)) {
  override def defaultController: PeriodicSignal[CollectorExtenderState] = Signal.constant(CollectorExtenderRetracted).toPeriodic

  lazy val gearTilter = gearTilterF()
  lazy val loadTray = loadTrayF()

  private var curLastExtendTime: Long = 0
  val lastExtendTime = Signal(curLastExtendTime)

  lazy val gearExtended = gearTilter.map(_.lastExtendTime.
    map(t => System.currentTimeMillis() - t <= 1000)).
    getOrElse(Signal.constant(false))

  lazy val loadTrayExtended = loadTray.map(_.lastExtendTime.
    map(t => System.currentTimeMillis() - t <= 1000)).
    getOrElse(Signal.constant(false))

  lazy val somethingExtended = gearExtended.zip(loadTrayExtended).map(t => t._1 || t._2)

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
