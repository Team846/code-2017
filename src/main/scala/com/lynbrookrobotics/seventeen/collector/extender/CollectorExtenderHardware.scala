package com.lynbrookrobotics.seventeen.collector.extender

import edu.wpi.first.wpilibj.Solenoid

case class CollectorExtenderHardware(pneumatic: Solenoid)

object CollectorExtenderHardware{
  def apply(config: CollectorExtenderConfig): CollectorExtenderHardware ={
    CollectorExtenderHardware(new Solenoid(config.port.pneumatic))
  }
}
