package com.lynbrookrobotics.seventeen.timing

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import edu.wpi.first.wpilibj.Utility
import squants.time.Milliseconds

import scala.reflect.io.File

/**
  * Created by the-magical-llamicorn on 5/1/17.
  */
class Timing(implicit clock: Clock) extends Component[Unit](Milliseconds(5)) {

  override def defaultController: PeriodicSignal[Unit] = Signal.constant().toPeriodic

  var discard = 250
  var ticks = 7500
  var lastTime = 0L
  val histogram = new Histogram(3000, 7000, 40)

  def applySignal(signal: Unit): Unit = {
    discard -= 1
    if(discard < 0 ) {
      ticks -= 1
      histogram.accept(Utility.getFPGATime - lastTime)
    }
    if (ticks == 0) {
      val writer = File(s"/tmp/${System.currentTimeMillis()}").printWriter()
      writer.println(histogram)
      writer.flush()
      writer.close()
    }
    lastTime = Utility.getFPGATime
  }
}
