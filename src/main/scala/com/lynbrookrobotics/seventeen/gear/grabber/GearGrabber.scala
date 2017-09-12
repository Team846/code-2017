package com.lynbrookrobotics.seventeen.gear.grabber

import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.{Component, Signal}
import com.lynbrookrobotics.seventeen.gear.tilter.GearTilter
import squants.time.Milliseconds

sealed trait GearGrabberState

case object GearGrabberOpen extends GearGrabberState

case object GearGrabberClosed extends GearGrabberState

class GearGrabber(val coreTicks: Stream[Unit])(implicit hardware: GearGrabberHardware, gearTilterF: () => Option[GearTilter])
  extends Component[GearGrabberState](Milliseconds(5)) {

  lazy val gearTilter = gearTilterF()

  override def defaultController: Stream[GearGrabberState] = coreTicks.mapToConstant(GearGrabberClosed)

  private var curLastOpenedTime: Long = 0
  val lastOpenTime = Signal(curLastOpenedTime)

  lazy val gearRetracted = gearTilter.map(_.lastRetractTime
    .map(t => System.currentTimeMillis() - t <= 50/*1000*/))
    .getOrElse(Signal.constant(true))

  override def applySignal(signal: GearGrabberState): Unit = {
    val outWithSafety = if (gearRetracted.get) {
      GearGrabberClosed
    } else {
      signal
    }

    hardware.pneumatic.set(outWithSafety == GearGrabberOpen)

    if (outWithSafety == GearGrabberOpen) {
      curLastOpenedTime = System.currentTimeMillis()
    }
  }
}
