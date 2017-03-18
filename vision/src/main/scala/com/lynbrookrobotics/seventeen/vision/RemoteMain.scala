package com.lynbrookrobotics.seventeen.vision

import scala.collection.mutable.Buffer
import com.lynbrookrobotics.potassium.clock._
import com.lynbrookrobotics.potassium.remote._
import com.lynbrookrobotics.potassium.vision._
import org.opencv.core._
import akka.actor._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.lynbrookrobotics.funkydashboard._
import com.lynbrookrobotics.seventeen.commons.VisionTargets

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import edu.wpi.cscore._

object RemoteMain extends App {
  override def main(args: Array[String]) = {
    implicit val actorSystem = ActorSystem("host")
    implicit val materializer = ActorMaterializer()

    val dashboard = Future {
      val dashboard = new FunkyDashboard
      Http().bindAndHandle(Route.handlerFlow(dashboard.route), "0.0.0.0", 8080).map { _ =>
        println("Funky Dashboard is up!")
        dashboard
      }
    }.flatten

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

    val leftCam = new UsbCamera("leftCam", 1)
    leftCam.setResolution(320, 240)

    val rightCam = new UsbCamera("rightCam", 2)
    rightCam.setResolution(320, 240)

    val driverCamServer = new MjpegServer("serve_driverCam", "0.0.0.0", 5803)
    driverCamServer.setSource(driverCam)

    val leftCamServer = new MjpegServer("serve_leftCam", "0.0.0.0", 5804)
    leftCamServer.setSource(leftCam)

    val rightCamServer = new MjpegServer("serve_rightCam", "0.0.0.0", 5805)
    rightCamServer.setSource(rightCam)
  }
}
