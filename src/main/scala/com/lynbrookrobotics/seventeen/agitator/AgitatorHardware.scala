package com.lynbrookrobotics.seventeen.agitator

import com.ctre.CANTalon

case class AgitatorHardware(motor: CANTalon)

object AgitatorHardware{
  def apply(config: AgitatorConfig): AgitatorHardware = {
    AgitatorHardware(new CANTalon(config.ports.motor))
  }
}