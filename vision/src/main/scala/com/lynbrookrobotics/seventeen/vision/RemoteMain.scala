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

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

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

    val leftProcessedProvider =
      new RemoteSignalProvider[Buffer[Rect]]("left-processed", VisionPipeline.leftCameraProcessed)
    val rightProcessedProvider =
      new RemoteSignalProvider[Buffer[Rect]]("right-processed", VisionPipeline.rightCameraProcessed)

    dashboard.foreach { board =>
      board.datasetGroup("Vision").addDataset(new ImageStream("Left Vision Output")(
        VisionPipeline.leftCamera.map(mat => VisionPipeline.matToBufferedImage(mat)).get
      ))

      board.datasetGroup("Vision").addDataset(new ImageStream("Right Vision Output")(
        VisionPipeline.rightCamera.map(mat => VisionPipeline.matToBufferedImage(mat)).get
      ))
    }
  }
}
