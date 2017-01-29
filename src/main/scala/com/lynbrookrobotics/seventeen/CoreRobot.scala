package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.seventeen.config.RobotConfig
import com.lynbrookrobotics.seventeen.hardware.DriverHardware
import com.lynbrookrobotics.funkydashboard.{FunkyDashboard, TimeSeriesNumeric}
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.frc.Implicits._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import edu.wpi.first.wpilibj.DriverStation
import squants.time.Milliseconds

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CoreRobot(implicit config: RobotConfig, clock: Clock) {
  implicit val driverHardware = DriverHardware(config.driver)

  val ds = DriverStation.getInstance()

  implicit val eventPolling = ds.onDataReceived

  val dashboard = Future {
    implicit val system = ActorSystem("funky-dashboard")
    implicit val materializer = ActorMaterializer()

    val dashboard = new FunkyDashboard

    Http().bindAndHandle(Route.handlerFlow(dashboard.route), "0.0.0.0", 8080).map { _ =>
      println("Funky Dashboard is up!")
      dashboard
    }
  }.flatten

  dashboard.foreach { board =>
    board.datasetGroup("Power").addDataset(new TimeSeriesNumeric("Battery Voltage")(
      ds.getBatteryVoltage
    ))
  }
}
