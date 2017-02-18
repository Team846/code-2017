package com.lynbrookrobotics.seventeen.lighting

import com.lynbrookrobotics.potassium.lighting.TwoWayComm
import edu.wpi.first.wpilibj.SerialPort

class SerialComms(serialPort: SerialPort) extends TwoWayComm{

  var connected = false;

  override def isConnected: Boolean = {
    connected
  }

  override def newData(int: Int): Unit = {
    try {
      serialPort.write(Array(int.toByte), 1)
      serialPort.readString()
      serialPort.flush()
    } catch {
      case e: Exception =>
    }
  }

  override def connect(): Unit = {
    try {
      serialPort.reset()
      connected = true
    } catch {
      case e: Exception => {
        connected = false
      }
    }
  }
  override def pullLog(): String = {
    try {
      serialPort.readString()
    } catch {
      case e: Exception => {
        e.printStackTrace()
        ""
      }
    }
  }
}
