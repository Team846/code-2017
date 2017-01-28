package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.seventeen.config.RobotConfig
import com.lynbrookrobotics.seventeen.hardware.DriverHardware
import com.lynbrookrobotics.funkydashboard.FunkyDashboard
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.frc.Implicits._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import edu.wpi.first.wpilibj.DriverStation
import squants.time.Milliseconds

class CoreRobot(implicit config: RobotConfig, clock: Clock) {
  implicit val driverHardware = DriverHardware(config.driver)

  implicit val eventPolling = DriverStation.getInstance().onDataReceived

  implicit val system = ActorSystem("funky-dashboard")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val dashboard = new FunkyDashboard

  Http().bindAndHandle(Route.handlerFlow(dashboard.route), "0.0.0.0", 8080)
}
