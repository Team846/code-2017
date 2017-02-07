package com.lynbrookrobotics.seventeen.climber.extender

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import com.lynbrookrobotics.seventeen.collector.extender.{CollectorExtender, CollectorExtenderExtended}
import com.lynbrookrobotics.seventeen.gear.collector.{GearCollector, GearCollectorExtended}
import squants.time.Milliseconds

sealed trait ClimberExtenderState
case object ClimberExtenderExtended extends ClimberExtenderState
case object ClimberExtenderRetracted extends ClimberExtenderState

class ClimberExtender(implicit hardware: ClimberExtenderHardware,
                      collectorExtender: CollectorExtender,
                      gearCollector: GearCollector,
                      clock: Clock) extends Component[ClimberExtenderState](Milliseconds(5)) {
  override def defaultController: PeriodicSignal[ClimberExtenderState] = Signal.constant(ClimberExtenderRetracted).toPeriodic

  override def applySignal(signal: ClimberExtenderState): Unit = {
    val otherExtended = collectorExtender.peekedController.get.contains(CollectorExtenderExtended) ||
      gearCollector.peekedController.get.contains(GearCollectorExtended)

    hardware.pneumatic.set(!otherExtended && (signal == ClimberExtenderExtended))
  }
}
