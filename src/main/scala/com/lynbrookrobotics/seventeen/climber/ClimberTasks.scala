package com.lynbrookrobotics.seventeen.climber

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.seventeen.climber.puller.{ClimberPuller, ClimberPullerProperties, RunPuller}

object ClimberTasks {
  def climb(implicit puller: ClimberPuller,
            pullerProps: Signal[ClimberPullerProperties],
            clock: Clock) = {
    new RunPuller()
  }
}
