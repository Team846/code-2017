package com.lynbrookrobotics.seventeen.lighting

import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.lighting.TwoWayComm
import com.lynbrookrobotics.potassium.streams.Stream
import squants.time.Milliseconds

class StatusLightingComponent(f: () => Int, comms: TwoWayComm, val coreTicks: Stream[Unit]) extends Component[() => Int] {
  override def defaultController: Stream[() => Int] = coreTicks.mapToConstant(f)

  def applySignal(signal: () => Int): Unit = {
    if (comms.isConnected) {
      comms.newData(f.apply())
    }
  }
}
