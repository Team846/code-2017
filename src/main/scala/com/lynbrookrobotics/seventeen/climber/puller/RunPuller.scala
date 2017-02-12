package com.lynbrookrobotics.seventeen.climber.puller

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class RunPuller(implicit puller: ClimberPuller, props: Signal[ClimberPullerProperties])
  extends ContinuousTask {
  override protected def onStart(): Unit = {
    puller.setController(props.map(p => PWMMode(p.climbSpeed)).toPeriodic)
  }

  override protected def onEnd(): Unit = {
    puller.resetToDefault()
  }
}
