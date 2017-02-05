package com.lynbrookrobotics.seventeen.shooter.feeder

import edu.wpi.first.wpilibj.Spark

case class ShooterFeederHardware(motor: Spark)

object ShooterFeederHardware {
  def apply(config: ShooterFeederConfig): ShooterFeederHardware = {
    ShooterFeederHardware(
      new Spark(config.ports.motorChannel)
    )
  }
}
