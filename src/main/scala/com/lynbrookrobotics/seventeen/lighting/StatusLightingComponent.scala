package com.lynbrookrobotics.seventeen.lighting

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.lighting.TwoWayComm
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import squants.time.{Milliseconds, Seconds}

class StatusLightingComponent(f: () => Int, comms: TwoWayComm, val coreTicks: Stream[Unit]) extends Component[() => Int](Milliseconds(5)) {
  override def defaultController: Stream[() => Int] = coreTicks.mapToConstant(f)

  def applySignal(signal: () => Int): Unit = {
    if (comms.isConnected) {
      comms.newData(f.apply())
    }
  }
}
