package com.lynbrookrobotics.seventeen.sensors.IMU

/**
  * Created by Nikash on 1/14/2017.
  *
  * Constructs a single datapoint from the IMU.
  */
case class IMUValue(gyro: Value3D, accel: Value3D, magneto: Value3D) {}
