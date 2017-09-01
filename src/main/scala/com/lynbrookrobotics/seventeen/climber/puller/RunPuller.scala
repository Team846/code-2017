package com.lynbrookrobotics.seventeen.climber.puller

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.potassium.streams.Stream
import squants.time.Seconds

class RunPuller(implicit puller: ClimberPuller, props: Signal[ClimberPullerProperties], clock: Clock)
  extends ContinuousTask {
  override protected def onStart(): Unit = {
    println("climbing!")
    puller.setController(Stream.periodic(Seconds(0.5))(PWMMode(props.get.climbSpeed)))
  }

  override protected def onEnd(): Unit = {
    puller.resetToDefault()
  }
}
