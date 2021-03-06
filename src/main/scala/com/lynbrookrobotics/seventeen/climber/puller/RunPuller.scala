package com.lynbrookrobotics.seventeen.climber.puller

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class RunPuller(puller: ClimberPuller)(implicit props: Signal[ClimberPullerProperties])
  extends ContinuousTask {
  val controlStream: Stream[ClimberControlMode] = puller.coreTicks.map(_ => PWMMode(props.get.climbSpeed))
  override protected def onStart(): Unit = {
    puller.setController(controlStream)
  }

  override protected def onEnd(): Unit = {
    puller.resetToDefault()
  }
}
