package com.lynbrookrobotics.seventeen.loadtray

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import com.lynbrookrobotics.seventeen.collector.extender.CollectorExtender
import squants.time.Milliseconds

sealed trait LoadTrayState
case object LoadTrayExtended extends LoadTrayState
case object LoadTrayRetracted extends LoadTrayState

class LoadTray(implicit hardware: LoadTrayHardware,
               collectorExtenderF: () => Option[CollectorExtender],
               clock: Clock) extends Component[LoadTrayState](Milliseconds(5)) {
  override def defaultController: PeriodicSignal[LoadTrayState] =
    Signal.constant(LoadTrayRetracted).toPeriodic

  lazy val collectorExtender = collectorExtenderF()

  private var curLastExtendTime: Long = 0
  val lastExtendTime = Signal(curLastExtendTime)

  lazy val collectorExtended = collectorExtender.map(_.lastExtendTime.
    map(t => System.currentTimeMillis() - t <= 1000)).
    getOrElse(Signal.constant(false))

  override def applySignal(signal: LoadTrayState): Unit = {
    if (collectorExtended.get) {
      hardware.pneumatic.set(false)
    } else {
      hardware.pneumatic.set(signal == LoadTrayExtended)

      if (signal == LoadTrayExtended) {
        curLastExtendTime = System.currentTimeMillis()
      }
    }
  }
}
