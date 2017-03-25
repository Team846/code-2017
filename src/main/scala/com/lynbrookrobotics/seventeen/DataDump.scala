package com.lynbrookrobotics.seventeen

import java.io.{File, PrintWriter}

import com.lynbrookrobotics.potassium.clock.JavaClock
import com.lynbrookrobotics.potassium.{Component, PeriodicSignal, Signal}
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import squants.Dimensionless
import squants.time.{Hertz, Milliseconds}

import scala.util.Random

class DataDump(sources: (String, Signal[Double])*) extends ContinuousTask {
  var stopCollection: () => Unit = () => Unit

  var fileName: String = ""
  var writer: PrintWriter = null

  var fileIter = 0

  override protected def onStart(): Unit = {
    fileName = "log_file" + System.currentTimeMillis.toString + fileIter
    fileIter = fileIter + 1

    writer = new PrintWriter(new File(s"/home/lvuser/$fileName"))

    println(s"Logging to $fileName...")

    sources.map{ case (columnName, signal) =>
        writer.print(columnName)
        if ((columnName, signal) != sources.last) writer.append(',')
    }

    writer.append('\n')

    stopCollection = JavaClock.apply(Milliseconds(5)){ _ =>
      sources.map{ case (columnName, signal) =>
        writer.print(signal.get)
        if ((columnName, signal) != sources.last) writer.append(',')

      }
      writer.append('\n')
    }
  }

  override protected def onEnd(): Unit = {
    println(s"Logged to $fileName")
    stopCollection
    writer.close()
  }
}