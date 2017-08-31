package com.lynbrookrobotics.seventeen.camselect

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class SelectCamera(camera: CamSelectState)(implicit camSelect: CamSelect) extends ContinuousTask {
  override def onStart(): Unit = {
    ???
    //camSelect.setController(Signal.constant(camera).toPeriodic)
  }

  override def onEnd(): Unit = {
    camSelect.resetToDefault()
  }
}
