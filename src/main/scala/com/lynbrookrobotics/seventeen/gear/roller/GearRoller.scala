package com.lynbrookrobotics.seventeen.gear.roller

import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.{Component, Signal}
import squants.Dimensionless
import squants.time.Milliseconds

class GearRoller(val coreTicks: Stream[Unit])(implicit val properties: Signal[GearRollerProperties],
                                              val hardware: GearRollerHardware)
  extends Component[Dimensionless](Milliseconds(5)) {

  override def defaultController: Stream[Dimensionless] = coreTicks.map(_ => properties.get.defaultHoldingPower)

  override def applySignal(signal: Dimensionless): Unit = {
    hardware.motor.set(signal.toEach)
  }
}
