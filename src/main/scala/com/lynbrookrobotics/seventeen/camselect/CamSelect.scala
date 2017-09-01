package com.lynbrookrobotics.seventeen.camselect

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.Component
import squants.time.{Milliseconds, Seconds}

trait CamSelectState

case object LeftCam extends CamSelectState

case object RightCam extends CamSelectState

case object DriverCam extends CamSelectState

class CamSelect(implicit hardware: CamSelectHardware, clock: Clock) extends Component[CamSelectState](Milliseconds(5)) {
  override def defaultController: Stream[CamSelectState] = Stream.periodic(Seconds(1))(DriverCam)
//    Signal.constant(DriverCam).toPeriodic

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
