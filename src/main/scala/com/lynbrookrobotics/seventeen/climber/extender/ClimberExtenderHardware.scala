package com.lynbrookrobotics.seventeen.climber.extender

import edu.wpi.first.wpilibj.Solenoid

case class ClimberExtenderHardware(pneumatic: Solenoid)

object ClimberExtenderHardware{
  def apply(config: ClimberExtenderConfig): ClimberExtenderHardware ={
    ClimberExtenderHardware(new Solenoid(config.port.pneumatic))
  }
}
