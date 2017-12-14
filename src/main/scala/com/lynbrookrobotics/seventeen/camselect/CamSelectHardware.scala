package com.lynbrookrobotics.seventeen.camselect

import edu.wpi.cscore.{HttpCamera, MjpegServer}
import edu.wpi.first.wpilibj.CameraServer

case class CamSelectHardware(leftCam: HttpCamera, rightCam: HttpCamera, driverCam: HttpCamera,
                              mjpegServer: MjpegServer)

object CamSelectHardware {
  def apply(config: CamSelectConfig): CamSelectHardware = {
    val uriBase = "http://" + config.properties.coprocessorHostname
    val uriPath = config.properties.mjpegPath

    val camSelectHardware = CamSelectHardware(
      new HttpCamera("leftCam", uriBase + s":${config.port.leftCamPort}" + uriPath),
      new HttpCamera("rightCam", uriBase + s":${config.port.rightCamPort}" + uriPath),
      new HttpCamera("driverCam", uriBase + s":${config.port.driveCamPort}" + uriPath),
      CameraServer.getInstance.addServer("serve_cam", 1180)
    )

    camSelectHardware.mjpegServer.setSource(camSelectHardware.driverCam)

    val cam = new HttpCamera("usbDriverCam", "http://10.8.64.2:1180/stream.mjpg")
    CameraServer.getInstance.startAutomaticCapture(cam)

    camSelectHardware
  }
}
