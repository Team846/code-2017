package com.lynbrookrobotics.seventeen.agitator

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.potassium.streams.Stream
import squants.time.Seconds

class SpinAgitator(implicit agitator: Agitator, agitatorProperties: Signal[AgitatorProperties], clock: Clock) extends ContinuousTask {
  override def onStart(): Unit = {
    agitator.setController(Stream.periodic(Seconds(0.01))(agitatorProperties.get.spinSpeed))
  }

  override def onEnd(): Unit = {
    agitator.resetToDefault()
  }
}
