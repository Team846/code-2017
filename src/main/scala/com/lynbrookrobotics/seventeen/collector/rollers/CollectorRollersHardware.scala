package com.lynbrookrobotics.seventeen.collector.rollers

import edu.wpi.first.wpilibj.Spark

case class CollectorRollersHardware(roller: Spark)

object CollectorRollersHardware {
  def apply(config: CollectorRollersConfig): CollectorRollersHardware = {
    CollectorRollersHardware(
      new Spark(config.ports.rollerChannel)
    )
  }
}
