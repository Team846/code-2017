package com.lynbrookrobotics.seventeen.gear.grabber

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import com.lynbrookrobotics.potassium.streams.Stream

import squants.time.Milliseconds

sealed trait GearGrabberState

case object GearGrabberOpen extends GearGrabberState

case object GearGrabberClosed extends GearGrabberState

class GearGrabber(implicit hardware: GearGrabberHardware,
                  clock: Clock) extends Component[GearGrabberState](Milliseconds(5)) {
  override def defaultController: Stream[GearGrabberState] = ??? // Signal.constant(GearGrabberClosed).toPeriodic

  override def applySignal(signal: GearGrabberState): Unit = {
    hardware.pneumatic.set(signal == GearGrabberOpen)
  }
}
