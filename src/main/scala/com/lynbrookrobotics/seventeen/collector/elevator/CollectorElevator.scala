package com.lynbrookrobotics.seventeen.collector.elevator

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, Signal}
import com.lynbrookrobotics.potassium.streams.Stream
import squants.time.{Milliseconds, Seconds}
import squants.{Dimensionless, Percent}

class CollectorElevator(val coreTicks: Stream[Unit])(implicit hardware: CollectorElevatorHardware)
  extends Component[Dimensionless](Milliseconds(5)) {
  override def defaultController = coreTicks.mapToConstant(Percent(0))

  override def applySignal(signal: Dimensionless): Unit = {
    hardware.motor.set(signal.toEach)
  }
}
