package com.lynbrookrobotics.seventeen.camselect

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.FiniteTask

class SelectCamera(camera: CamSelectState)(implicit camSelect: CamSelect) extends FiniteTask {
  override def onStart(): Unit = {
    camSelect.setController(Signal.constant(camera).toPeriodic)
    finished()
  }

  override def onEnd(): Unit = { }
}
