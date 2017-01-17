package com.lynbrookrobotics.seventeen.sensors.IMU

import java.nio.ByteBuffer
import edu.wpi.first.wpilibj.SPI

/**
  * Created by Nikash on 1/14/2017.
  *
  * Implements the IMU protocol for the robot.
  */
class ADIS16448Protocol {
  private object Registers {
    // List of register addresses on the IMU
    final val SMPL_PRD: IMURegister = IMURegister(0x36)
    final val SENS_AVG: IMURegister = IMURegister(0x38)
    final val MSC_CTRL: IMURegister = IMURegister(0x34)
    final val PROD_ID: IMURegister = IMURegister(0x56)

  }
    // Private object used to substitute for private values in Java
   private object ADIS16448Protocol {
     final val X_GYRO_REG: Byte = 0x04
     final val Y_GYRO_REG: Byte = 0x06
     final val Z_GYRO_REG: Byte = 0x08
   }

    // Private object used to substitute for private values in Java
    // Contains gyro- specific constants, should be unchanged for new robot
    // LSB: Least Significant Bits
  private object Constants {
    final val DegreePerSecondPerLSB: Double = 1.0 / 25.0
    final val GPerLSB: Double = 1.0 / 1200.0
    final val MilligaussPerLSB: Double = 1.0 / 7.0
  }
    //
  private val spi: ConstantBufferSPI = new ConstantBufferSPI(SPI.Port.kMXP, 2)
  spi.setClockRate(3000000)
  spi.setMSBFirst()
  spi.setSampleDataOnFalling()
  spi.setClockActiveLow()
  spi.setChipSelectActiveLow()

  Registers.PROD_ID.read(spi)

    // Checks whether or not the IMU connected uses the 16448 SPI
  if (Registers.PROD_ID.read(spi) != 16448) {
    throw new IllegalStateException("The device in the MXP port is not an ADIS16448 IMU")
  }
    // TODO: What are Magic Numbers??
    // Saves the SPI being used (16448) to the various registers
  Registers.SMPL_PRD.write(1, spi) // TODO: Magic Number
  Registers.MSC_CTRL.write(4, spi) // TODO: Magic Number
  Registers.SENS_AVG.write(Integer.parseInt("10000000000", 2), spi) // TODO: Magic Number

  private val outBuffer: ByteBuffer = ByteBuffer.allocateDirect(2)
  private val inBuffer: ByteBuffer = ByteBuffer.allocateDirect(2)

    // TODO: What is happening here?
  private def readGyroRegister(register: Byte): Short = {
    outBuffer.put(0, register)
    outBuffer.put(1, 0.asInstanceOf[Byte])
    spi.write(outBuffer, 2)

    inBuffer.clear
    spi.read(false, inBuffer, 2)

    inBuffer.getShort
  }

  // Gets the current gyro, accel, and magneto data from the IMU.
  // 2nd and 3rd parameters are null because only gyro is used.
  def currentData: IMUValue = {
    val gyro: Value3D = Value3D(
      readGyroRegister(ADIS16448Protocol.X_GYRO_REG),
      readGyroRegister(ADIS16448Protocol.Y_GYRO_REG),
      readGyroRegister(ADIS16448Protocol.Z_GYRO_REG)
    ).times(Constants.DegreePerSecondPerLSB)
    IMUValue(gyro, null, null)
  }
}
