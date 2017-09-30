package com.lynbrookrobotics.seventeen.loadtray

import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class ExtendTray(loadTray: LoadTray) extends ContinuousTask {
  override protected def onStart(): Unit = {
    loadTray.setController(loadTray.coreTicks.mapToConstant(LoadTrayExtended))
  }

  override protected def onEnd(): Unit = {
    loadTray.resetToDefault()
  }
}
