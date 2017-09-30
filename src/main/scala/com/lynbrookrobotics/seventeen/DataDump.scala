package com.lynbrookrobotics.seventeen

import java.io.PrintWriter

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.tasks.ContinuousTask

class DataDump(sources: (String, Signal[Double])*) extends ContinuousTask {
  var stopCollection: () => Unit = null

  var fileName: String = ""
  var writer: PrintWriter = null

  var fileIter = 0

  override protected def onStart(): Unit = {
//    fileName = "log_file" + System.currentTimeMillis.toString + fileIter
//    fileIter = fileIter + 1
//
//    writer = new PrintWriter(new File(s"/home/lvuser/$fileName"))
//
//    println(s"Logging to $fileName...")
//
//    writer.println(sources.map(_._1).mkString(","))
//    writer.flush()
//
//    stopCollection = WPIClock(Milliseconds(5)) { _ =>
//      writer.println(sources.map(_._2.get).mkString(","))
//      writer.flush()
//    }
  }

  override protected def onEnd(): Unit = {
//    println(s"Logged to $fileName")
//    stopCollection.apply()
//    writer.close()
  }
}