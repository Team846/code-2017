package com.lynbrookrobotics.seventeen.vision

import com.lynbrookrobotics.funkydashboard._
import edu.wpi.cscore._

import scala.util.Try

object RemoteMain extends App {
  val dashboard = Try {
    val dashboard = new FunkyDashboard(125, 80808)
    dashboard.start()
    dashboard
  }

  // val leftProcessedProvider =
  //   new RemoteSignalProvider[VisionTargets]("left-processed", VisionPipeline.leftCameraProcessed)
  // val rightProcessedProvider =
  //   new RemoteSignalProvider[VisionTargets]("right-processed", VisionPipeline.rightCameraProcessed)

  // dashboard.foreach { board =>
  //   board.datasetGroup("Vision").addDataset(new ImageStream("Left Vision Output")(
  //     VisionPipeline.leftCamera.map(timestampedMat =>
  //       VisionPipeline.matToBufferedImage(timestampedMat.mat)).get
  //   ))

  //   board.datasetGroup("Vision").addDataset(new ImageStream("Right Vision Output")(
  //     VisionPipeline.rightCamera.map(timestampedMat =>
  //       VisionPipeline.matToBufferedImage(timestampedMat.mat)).get
  //   ))
  // }

  val driverCam = new UsbCamera("driverCam", 0) // TODO: use v4l paths
  driverCam.setResolution(320, 240)

//    val leftCam = new UsbCamera("leftCam", 1)
//    leftCam.setResolution(320, 240)
//
//    val rightCam = new UsbCamera("rightCam", 2)
//    rightCam.setResolution(320, 240)

  val driverCamServer = new MjpegServer("serve_driverCam", "", 5803)
  driverCamServer.setSource(driverCam)

  val leftCamServer = new MjpegServer("serve_leftCam", "", 5804)
//    leftCamServer.setSource(leftCam)

  val rightCamServer = new MjpegServer("serve_rightCam", "", 5805)
//    rightCamServer.setSource(rightCam)
}
