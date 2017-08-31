package com.lynbrookrobotics.seventeen.collector.elevator

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, Signal}
import com.lynbrookrobotics.potassium.streams.Stream
import squants.time.Milliseconds
import squants.{Dimensionless, Percent}

class CollectorElevator(implicit hardware: CollectorElevatorHardware, clock: Clock)
  extends Component[Dimensionless](Milliseconds(5)) {
  override def defaultController = Stream.periodic(Milliseconds(5))(Percent(0))(???)

  override def applySignal(signal: Dimensionless): Unit = {
    hardware.motor.set(signal.toEach)
  }
}
