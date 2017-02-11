package com.lynbrookrobotics.seventeen.climber

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.seventeen.climber.puller.{ClimberPuller, ClimberPullerProperties, RunPuller}

object ClimberTasks {
  def climb(implicit puller: ClimberPuller,
            pullerProps: Signal[ClimberPullerProperties]) = {
    new RunPuller()
  }
}
