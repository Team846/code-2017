package com.lynbrookrobotics.seventeen.agitator

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.electronics.CurrentLimiting
import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.streams.Stream
import squants.time.{Milliseconds, Seconds}
import squants.{Dimensionless, Each, Percent}

class Agitator(implicit hardware: AgitatorHardware, clock: Clock) extends Component[Dimensionless](Milliseconds(5)) {
  override def defaultController: Stream[Dimensionless] = Stream.periodic(Seconds(1)) {
    Each(0)
  }

  override def setController(controller: Stream[Dimensionless]): Unit = {
    super.setController(CurrentLimiting.slewRate(controller, Percent(100) / Seconds(0.3)))
  }

  override def applySignal(signal: Dimensionless): Unit = {
    hardware.motor.set(signal.toEach)
  }
}
