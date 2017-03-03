package com.lynbrookrobotics.seventeen.camselect

import edu.wpi.cscore.{ HttpCamera, MjpegServer }

case class CamSelectHardware(
  leftCam: HttpCamera, rightCam: HttpCamera, driverCam: HttpCamera,
  mjpegServer: MjpegServer
)

object CamSelectHardware {
  def apply(config: CamSelectConfig): CamSelectHardware = {
    val url = "http://" + config.properties.coprocessorHostname + config.properties.mjpegPath

    CamSelectHardware(
      new HttpCamera("leftCam", url, HttpCamera.HttpCameraKind.kMJPGStreamer),
      new HttpCamera("rightCam", url, HttpCamera.HttpCameraKind.kMJPGStreamer),
      new HttpCamera("driverCam", url, HttpCamera.HttpCameraKind.kMJPGStreamer),
      new MjpegServer("serve_Camera Selection", 1182)
    )
  }
}
