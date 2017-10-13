package com.lynbrookrobotics.seventeen.gear.roller

import com.lynbrookrobotics.potassium.commons.electronics.CurrentLimiting
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.{Component, Signal}
import squants.{Dimensionless, Percent}
import squants.time.{Milliseconds, Seconds}

class GearRoller(val coreTicks: Stream[Unit])(implicit val properties: Signal[GearRollerProperties],
                                              val hardware: GearRollerHardware)
  extends Component[Dimensionless](Milliseconds(5)) {
  var disabledAutoRun = false

  override def setController(controller: Stream[Dimensionless]): Unit = {
    super.setController(CurrentLimiting.slewRate(controller, Percent(100) / Seconds(0.3)))
  }

  override def defaultController: Stream[Dimensionless] = coreTicks.map(_ => {
    if (disabledAutoRun) Percent(0) else properties.get.defaultHoldingPower
  })

  override def applySignal(signal: Dimensionless): Unit = {
    hardware.motor.set(signal.toEach)
  }
}
