package com.lynbrookrobotics.seventeen.camselect

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import squants.time.Milliseconds

trait CamSelectState
case object LeftCam extends CamSelectState
case object RightCam extends CamSelectState
case object DriverCam extends CamSelectState

class CamSelect(implicit hardware: CamSelectHardware, clock: Clock) extends Component[CamSelectState](Milliseconds(5)) {
  override def defaultController: PeriodicSignal[CamSelectState] = Signal.constant(DriverCam).toPeriodic

  override def applySignal(signal: CamSelectState): Unit = {
    signal match {
      case LeftCam => hardware.mjpegServer.setSource(hardware.leftCam)
      case RightCam => hardware.mjpegServer.setSource(hardware.rightCam)
      case DriverCam => hardware.mjpegServer.setSource(hardware.driverCam)
    }
  }
}
