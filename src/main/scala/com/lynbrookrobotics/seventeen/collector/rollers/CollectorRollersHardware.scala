package com.lynbrookrobotics.seventeen.collector.rollers

import edu.wpi.first.wpilibj.Spark

case class CollectorRollersHardware(rollerA: Spark, rollerB: Spark)

object CollectorRollersHardware {
  def apply(config: CollectorRollersConfig): CollectorRollersHardware = {
    CollectorRollersHardware(
      new Spark(config.ports.rollerChannelA),
      new Spark(config.ports.rollerChannelB)
    )
  }
}
