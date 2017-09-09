package com.lynbrookrobotics.seventeen.climber.puller

import com.ctre.CANTalon

case class ClimberPullerHardware(motorA: CANTalon, motorB: CANTalon)

object ClimberPullerHardware {
  def apply(config: ClimberPullerConfig): ClimberPullerHardware = {
    val a = new CANTalon(config.ports.motorChannelA)
    val b = new CANTalon(config.ports.motorChannelB)

    ClimberPullerHardware(a, b)
  }
}
