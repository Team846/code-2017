package com.lynbrookrobotics.seventeen.loadtray

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.seventeen.collector.extender.CollectorExtender
import squants.time.{Milliseconds, Seconds}

sealed trait LoadTrayState

case object LoadTrayExtended extends LoadTrayState

case object LoadTrayRetracted extends LoadTrayState

class LoadTray(val coreTicks: Stream[Unit])(implicit hardware: LoadTrayHardware) extends Component[LoadTrayState](Milliseconds(5)) {
  override def defaultController: Stream[LoadTrayState] = coreTicks.mapToConstant(LoadTrayRetracted)

  override def applySignal(signal: LoadTrayState): Unit = {
    hardware.pneumatic.set(signal == LoadTrayExtended)
  }
}
