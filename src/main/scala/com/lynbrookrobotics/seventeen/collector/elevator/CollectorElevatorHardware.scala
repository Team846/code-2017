package com.lynbrookrobotics.seventeen.collector.elevator

import edu.wpi.first.wpilibj.Spark

case class CollectorElevatorHardware(motor: Spark)

object CollectorElevatorHardware {
  def apply(config: CollectorElevatorConfig): CollectorElevatorHardware = {
    CollectorElevatorHardware(new Spark(config.port.motor))
  }
}
