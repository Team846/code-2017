package com.lynbrookrobotics.seventeen.gear.collector

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import com.lynbrookrobotics.seventeen.climber.extender.{ClimberExtender, ClimberExtenderExtended}
import com.lynbrookrobotics.seventeen.collector.extender.{CollectorExtender, CollectorExtenderExtended}
import squants.time.Milliseconds

sealed trait GearCollectorState
case object GearCollectorExtended extends GearCollectorState
case object GearCollectorRetracted extends GearCollectorState

class GearCollector(implicit hardware: GearCollectorHardware,
                    climberExtender: ClimberExtender,
                    collectorExtender: CollectorExtender,
                    clock: Clock) extends Component[GearCollectorState](Milliseconds(5)){
  override def defaultController: PeriodicSignal[GearCollectorState] = Signal.constant(GearCollectorRetracted).toPeriodic


  override def applySignal(signal: GearCollectorState): Unit = {
    val otherExtended = climberExtender.peekedController.get.contains(ClimberExtenderExtended) ||
      collectorExtender.peekedController.get.contains(CollectorExtenderExtended)

    hardware.pneumatic.set(!otherExtended && signal == GearCollectorExtended)
  }
}
