package com.lynbrookrobotics.seventeen.camselect

import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.streams.Stream
import squants.time.Milliseconds

trait CamSelectState

case object LeftCam extends CamSelectState

case object RightCam extends CamSelectState

case object DriverCam extends CamSelectState

class CamSelect(val coreTicks: Stream[Unit])(implicit hardware: CamSelectHardware) extends Component[CamSelectState](Milliseconds(5)) {
  override def defaultController: Stream[CamSelectState] = coreTicks.mapToConstant(DriverCam)

  override def applySignal(signal: CamSelectState): Unit = {
    if (hardware != null) {
      signal match {
        case LeftCam => hardware.mjpegServer.setSource(hardware.leftCam)
        case RightCam => hardware.mjpegServer.setSource(hardware.rightCam)
        case DriverCam => hardware.mjpegServer.setSource(hardware.driverCam)
      }
    }
  }
}
