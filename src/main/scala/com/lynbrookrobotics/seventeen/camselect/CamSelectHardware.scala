package com.lynbrookrobotics.seventeen.camselect

import edu.wpi.cscore.{ HttpCamera, MjpegServer }

case class CamSelectHardware(
  leftCam: HttpCamera, rightCam: HttpCamera, driverCam: HttpCamera,
  mjpegServer: MjpegServer
)

object CamSelectHardware {
  def apply(config: CamSelectConfig): CamSelectHardware = {
    val uriBase = "http://" + config.properties.coprocessorHostname
    val uriPath = config.properties.mjpegPath

    CamSelectHardware(
      new HttpCamera("leftCam", uriBase + s":${config.ports.leftCamPort}" + uriPath, HttpCamera.HttpCameraKind.kMJPGStreamer),
      new HttpCamera("rightCam", uriBase + s":${config.ports.rightCamPort}" + uriPath, HttpCamera.HttpCameraKind.kMJPGStreamer),
      new HttpCamera("driverCam", uriBase + s":${config.ports.driverCamPort}" + uriPath, HttpCamera.HttpCameraKind.kMJPGStreamer),
      new MjpegServer("serve_Camera Selection", 1182)
    )
  }
}
