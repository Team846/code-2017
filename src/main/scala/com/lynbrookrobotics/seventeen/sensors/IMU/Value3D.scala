package com.lynbrookrobotics.seventeen.sensors.IMU

/**
  * Created by Nikash on 1/14/2017.
  */

/**
  * Constructs a new 3D value given X, Y, and Z axes.
  */

case class Value3D(valueX: Double, valueY: Double, valueZ: Double) {

  def set(src: Value3D): Unit = {
    valueX += src.valueX
    valueY += src.valueY
    valueZ += src.valueZ
  }

  /**
    * Adds this 3D value to another one.
    * @param toAdd the 3D value to add
    * @return the combined 3D value
    */
  def plus(toAdd: Value3D): Value3D = {
  Value3D(
    valueX + toAdd.valueX,
    valueY + toAdd.valueY,
    valueZ + toAdd. valueZ
  )
  }
  /**
    * Adds the given values to each axis.
    */
  def plusMutable(addX: Double, addY: Double, addZ: Double): Unit = {
    valueX += addX
    valueY += addY
    valueZ += addZ
  }
  /**
    * Multiplies this 3D value by a scalar.
    * @param scalar the value to multiply the axes by
    * @return the scaled 3D value
    */
  def times(scalar: Double): Value3D = {
    Value3D(
    scalar * valueX,
    scalar * valueY,
    scalar * valueZ
    )
  }
  /**
    * Multiplies this 3D value by a scalar.
    * @param scalar the value to multiply the axes by
    */
  def timesMutable(scalar: Double): Unit = {
    valueX *= scalar
    valueY *= scalar
    valueZ *= scalar
  }
}
