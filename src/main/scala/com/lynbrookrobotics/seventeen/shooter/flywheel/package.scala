package com.lynbrookrobotics.seventeen.shooter

import com.lynbrookrobotics.potassium.commons.flywheel.DoubleFlywheel

package object flywheel extends DoubleFlywheel {
  override type Properties = ShooterFlywheelProperties
  override type Hardware = ShooterFlywheelHardware

  override def outputSignal(s: DoubleFlywheelSignal)(implicit hardware: Hardware): Unit = ???

  override type Comp = ShooterFlywheel
}
