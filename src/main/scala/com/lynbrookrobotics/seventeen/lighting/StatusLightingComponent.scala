package com.lynbrookrobotics.seventeen.lighting

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.lighting.TwoWayComm
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import squants.time.Milliseconds

class StatusLightingComponent(f: () => Int, comms: TwoWayComm)(implicit clock: Clock) extends Component[() => Int](Milliseconds(5)) {
  override def defaultController: Stream[() => Int] = ??? //Signal.constant(f).toPeriodic

  def applySignal(signal: () => Int): Unit = {
    if (comms.isConnected) {
      comms.newData(f.apply())
    }
  }
}
