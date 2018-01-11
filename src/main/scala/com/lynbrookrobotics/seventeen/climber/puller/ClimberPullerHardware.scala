package com.lynbrookrobotics.seventeen.climber.puller

import com.ctre.phoenix.motorcontrol.can.TalonSRX

case class ClimberPullerHardware(motorA: TalonSRX, motorB: TalonSRX)

object ClimberPullerHardware {
  def apply(config: ClimberPullerConfig): ClimberPullerHardware = {
    val a = new TalonSRX(config.ports.motorChannelA)
    val b = new TalonSRX(config.ports.motorChannelB)

    a.setInverted(true)
    b.setInverted(true)

    ClimberPullerHardware(a, b)
  }
}
