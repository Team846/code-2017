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

  lazy val gearExtended = gearTilter.map(_.lastExtendTime
    .map(t => System.currentTimeMillis() - t <= 1000))
    .getOrElse(Signal.constant(false))

  override def applySignal(signal: GearGrabberState): Unit = {
    if (!gearExtended.get) {
      hardware.pneumatic.set(false)
    } else {
      hardware.pneumatic.set(signal == GearGrabberOpen)

      if (signal == GearGrabberOpen) {
        curLastOpenedTime = System.currentTimeMillis()
      }
    }
  }
}
