package com.lynbrookrobotics.seventeen.shooter

import com.lynbrookrobotics.potassium.commons.flywheel.DoubleFlywheel

package object flywheel extends DoubleFlywheel {
  override type Properties = ShooterFlywheelProperties
  override type Hardware = ShooterFlywheelHardware

  override type Comp = ShooterFlywheel
}
