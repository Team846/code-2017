package com.lynbrookrobotics.seventeen.climber.puller

import com.ctre.CANTalon

case class ClimberPullerHardware(motorA: CANTalon, motorB: CANTalon)

object ClimberPullerHardware {
  def apply(config: ClimberPullerConfig): ClimberPullerHardware = {
    ClimberPullerHardware(
      {
        val it = new CANTalon(config.ports.motorChannelA)
        it.setInverted(true)
        it
      }, {
        val it = new CANTalon(config.ports.motorChannelB)
        it.setInverted(true)
        it
      }
    )
  }
}
