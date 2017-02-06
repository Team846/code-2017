package com.lynbrookrobotics.seventeen.climber

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.seventeen.climber.extender.{ClimberExtender, ExtendClimber}
import com.lynbrookrobotics.seventeen.climber.puller.{ClimberPuller, ClimberPullerProperties, RunPuller}

object ClimberTasks {
  def climb(implicit puller: ClimberPuller,
            extender: ClimberExtender,
            pullerProps: Signal[ClimberPullerProperties]) = {
    new ExtendClimber().and(new RunPuller())
  }
}
