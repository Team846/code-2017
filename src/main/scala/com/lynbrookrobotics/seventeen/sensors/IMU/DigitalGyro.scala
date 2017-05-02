package com.lynbrookrobotics.seventeen.sensors.IMU


import java.util

/**
  * Created by Nikash on 1/14/2017.
  */
abstract class DigitalGyro {
  final var TICK_PERIOD: Double = ??? //TODO set value based on final 2017 robot

  var currentVelocity: Value3D = Value3D(0, 0, 0)
  var currentPosition: Value3D = Value3D(0,0,0)

  var currentDrift: Value3D = _

  var values: util.ArrayList[Value3D] = new util.ArrayList(200)
  var index: Int = 0
  var calibrating: Boolean = true

  // Gets the current velocity.
  def retrieveVelocity(): Value3D

  // Updates values for the drift on the axis.
  def calibrateUpdate(): Unit ={
    index += 1
    currentVelocity.set(retrieveVelocity())
    values.add(index, currentVelocity)

    if(index > 200) {
      index = 0
    }
  }

  // Updates values for the angle on the gyro.
  def angleUpdate(): Unit = {
    if(calibrating) {
      val sum: Value3D = Value3D(0,0,0)
      values.forEach(
        value3D => sum.plusMutable(
          value3D.valueX,
          value3D.valueY,
          value3D.valueZ
        )
      )
      currentDrift = sum.times(-1D / values.size())
      values = null

      calibrating = false
    }

    // Stores the value as a form of memory
    // Modifies velocity according to drift and change in position
    val previousVelocity: Value3D = currentVelocity
    currentVelocity.set(retrieveVelocity().plus(currentDrift))
    currentPosition.set(currentPosition
      .plus(Value3D(
        trapezoidalIntegration(currentVelocity.valueX, previousVelocity.valueX),
        trapezoidalIntegration(currentVelocity.valueY, previousVelocity.valueY),
        trapezoidalIntegration(currentVelocity.valueZ, previousVelocity.valueZ)
      )))

  }
  // Defines calculations for velocity adjustments
  private def trapezoidalIntegration(velocity: Double, previousVelocity: Double): Double = {
    TICK_PERIOD * ((velocity + previousVelocity) / 2)
  }
}
