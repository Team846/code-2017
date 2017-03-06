package com.lynbrookrobotics.seventeen.camselect

import edu.wpi.cscore.{ HttpCamera, MjpegServer, UsbCamera, VideoSource }
import edu.wpi.first.wpilibj.CameraServer

case class CamSelectHardware(
  leftCam: HttpCamera, rightCam: HttpCamera, driverCam: HttpCamera,
  mjpegServer: MjpegServer
)

object CamSelectHardware {
  def apply(config: CamSelectConfig): CamSelectHardware = {
    val cam = new HttpCamera("usbDriverCam", "http://10.8.46.2:1180/stream.mjpg")

    CameraServer.getInstance()
    CameraServer.getInstance.startAutomaticCapture(cam)

    val uriBase = "http://" + config.properties.coprocessorHostname
    val uriPath = config.properties.mjpegPath

    val camSelectHardware = CamSelectHardware(
      new HttpCamera("leftCam", uriBase + s":${config.port.leftCamPort}" + uriPath),
      new HttpCamera("rightCam", uriBase + s":${config.port.rightCamPort}" + uriPath),
      new HttpCamera("driverCam", uriBase + s":${config.port.driveCamPort}" + uriPath),
      CameraServer.getInstance.addServer("serve_Camera Selection", 1180)
    )

    camSelectHardware
  }
}
