package com.lynbrookrobotics.seventeen.climber.puller

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.potassium.streams.Stream
import squants.time.Seconds

class RunPuller(implicit puller: ClimberPuller, props: Signal[ClimberPullerProperties])
  extends ContinuousTask {
  val controlStream: Stream[ClimberControlMode] = puller.coreTicks.map(_ => PWMMode(props.get.climbSpeed))
  override protected def onStart(): Unit = {
    puller.setController(controlStream)
  }

  override protected def onEnd(): Unit = {
    puller.resetToDefault()
  }
}
