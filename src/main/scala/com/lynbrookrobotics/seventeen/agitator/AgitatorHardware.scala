package com.lynbrookrobotics.seventeen.agitator

import edu.wpi.first.wpilibj.Spark

case class AgitatorHardware(motor: Spark)

object AgitatorHardware {
  def apply(config: AgitatorConfig): AgitatorHardware = {
    AgitatorHardware(new Spark(config.ports.motor))
  }
}
