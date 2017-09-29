package com.lynbrookrobotics.seventeen.agitator

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.potassium.streams.Stream
import squants.time.Seconds

class SpinAgitator(agitator: Agitator)(implicit agitatorProperties: Signal[AgitatorProperties]) extends ContinuousTask {
  override def onStart(): Unit = {
    agitator.setController(agitator.coreTicks.map(_ => agitatorProperties.get.spinSpeed))
  }

  override def onEnd(): Unit = {
    agitator.resetToDefault()
  }
}
