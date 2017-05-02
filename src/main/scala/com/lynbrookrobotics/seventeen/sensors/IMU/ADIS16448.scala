package com.lynbrookrobotics.seventeen.sensors.IMU

/**
  * Created by Nikash on 1/14/2017.
  *
  * An interface for communicating with the ADIS16448 IMU.
  */

class ADIS16448 extends DigitalGyro {


  private val imuCom: ADIS16448Protocol = new ADIS16448Protocol
  @Override

  /**
    * Retrieves 3-dimensional data from the gyro
    */
  def retrieveVelocity(): Value3D = {
    imuCom.currentData.gyro
  }
}
