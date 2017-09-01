package com.lynbrookrobotics.seventeen.camselect

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.potassium.streams.Stream
import squants.time.Seconds

class SelectCamera(camera: CamSelectState)(implicit camSelect: CamSelect, clock: Clock) extends ContinuousTask {
  override def onStart(): Unit = {
    camSelect.setController(Stream.periodic(Seconds(0.5))(camera))
  }

  override def onEnd(): Unit = {
    camSelect.resetToDefault()
  }
}
