package com.lynbrookrobotics.seventeen.sensors.IMU

import java.nio.ByteBuffer

import edu.wpi.first.wpilibj.SPI
import edu.wpi.first.wpilibj.SPI._
import edu.wpi.first.wpilibj.hal.SPIJNI
/**
  * Created by Nikash on 1/15/2017.
  *
  * Constructs an SPI interface with constant size I/O buffer.
  * @param port the physical SPI port
  * @param size the max size of I/O
  */
class ConstantBufferSPI(port: Port, size: Int) extends SPI(port) {
  private val sendBuffer: ByteBuffer = ByteBuffer.allocateDirect(size)
  private val receiveBuffer: ByteBuffer= ByteBuffer.allocateDirect(size)
  private val port1: Byte = port.value.asInstanceOf[Byte]

  override final def transaction(dataToSend: Array[Byte], dataReceived: Array[Byte], size: Int): Int = {
    sendBuffer.clear()
    receiveBuffer.clear()

    sendBuffer.put(dataToSend)
    val resultCode: Int = SPIJNI.spiTransaction(port1, sendBuffer, receiveBuffer, size.asInstanceOf[Byte])
    receiveBuffer.get(dataReceived)

    resultCode
  }
}
