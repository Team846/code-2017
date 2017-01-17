package com.lynbrookrobotics.seventeen.sensors.IMU

/**
  * Import statements for SPI and code involving Bytes
  */

import java.nio.ByteBuffer
import edu.wpi.first.wpilibj.SPI

/**
  * Created by Nikash on 1/14/2017.
  */
/**
  * Represents a register on the ADIS16448 IMU.
  */

/**
  *
  * @param register is ID of register on IMU
  */
case class IMURegister(register: Int) {
  private val readBuffer: ByteBuffer = ByteBuffer.allocateDirect(2)
  // note that casting is not supported by Scala, so must use .asInstanceOf[]
  // Arrays not in-built in Scala, so must be defined separately
  private val readMessage: Array[Byte] = Array((register & 0x7f).asInstanceOf[Byte], 0.asInstanceOf[Byte])
  private val writeMessage1: Byte = (register | 0x80).asInstanceOf[Byte]
  private val writeMessage2: Byte = (register | 0x81).asInstanceOf[Byte]

  /**
    * Reads a value from the register.
    * @param spi the interface to use for communication
    * @return a single value from the register
    */
  def read(spi: SPI): Int = {
    readBuffer.clear()
    spi.write(readMessage, 2)
    spi.read(false, readBuffer, 2)

    readBuffer.getShort(0).asInstanceOf[Int] & 0xffff

  }

  /**
    * Writes a single value to the register.
    *
    * @param value the value to write
    * @param spi   the interface to use for communication
    */

  def write(value: Int, spi: SPI): Unit = {
    spi.write(Array(writeMessage1, value.asInstanceOf[Byte]), 2.asInstanceOf[Byte])
    spi.write(Array(writeMessage2, (value >> 8).asInstanceOf[Byte]), 2.asInstanceOf[Byte])
  }

}
