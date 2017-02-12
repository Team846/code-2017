package com.lynbrookrobotics.seventeen.collector.extender

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import com.lynbrookrobotics.seventeen.climber.extender.{ClimberExtender, ClimberExtenderExtended}
import com.lynbrookrobotics.seventeen.gear.collector.{GearCollector, GearCollectorExtended}
import squants.time.Milliseconds

sealed trait CollectorExtenderState
case object CollectorExtenderExtended extends CollectorExtenderState
case object CollectorExtenderRetracted extends CollectorExtenderState

class CollectorExtender(implicit hardware: CollectorExtenderHardware,
                        climberExtender: ClimberExtender,
                        gearCollector: GearCollector,
                        clock: Clock) extends Component[CollectorExtenderState](Milliseconds(5)) {
  override def defaultController: PeriodicSignal[CollectorExtenderState] = Signal.constant(CollectorExtenderRetracted).toPeriodic

  override def applySignal(signal: CollectorExtenderState): Unit = {
    val otherExtended = climberExtender.peekedController.get.contains(ClimberExtenderExtended) ||
      gearCollector.peekedController.get.contains(GearCollectorExtended)

    hardware.pneumatic.set(!otherExtended && signal == CollectorExtenderExtended)
  }
}
