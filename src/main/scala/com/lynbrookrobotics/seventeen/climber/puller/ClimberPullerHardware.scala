package com.lynbrookrobotics.seventeen.climber.puller

import edu.wpi.first.wpilibj.Spark

case class ClimberPullerHardware(motorA: Spark, motorB: Spark)

object ClimberPullerHardware {
  def apply(config: ClimberPullerConfig): ClimberPullerHardware = {
    ClimberPullerHardware(
      new Spark(config.ports.motorChannelA),
      new Spark(config.ports.motorChannelB)
    )
  }
}
