package com.lynbrookrobotics.seventeen.climber.puller

import com.ctre.CANTalon

case class ClimberPullerHardware(motorA: CANTalon, motorB: CANTalon)

object ClimberPullerHardware {
  def apply(config: ClimberPullerConfig): ClimberPullerHardware = {
    ClimberPullerHardware(
      new CANTalon(config.ports.motorChannelA),
      new CANTalon(config.ports.motorChannelB)
    )
  }
}
