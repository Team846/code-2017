package com.lynbrookrobotics.seventeen.camselect

import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class SelectCamera(camera: CamSelectState)(implicit camSelect: CamSelect) extends ContinuousTask {
  override def onStart(): Unit = {
    camSelect.setController(camSelect.coreTicks.mapToConstant(camera))
  }

  override def onEnd(): Unit = {
    camSelect.resetToDefault()
  }
}
