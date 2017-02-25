package com.lynbrookrobotics.seventeen.shooter.flywheel

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.commons.flywheel.DoubleFlywheelHardware
import edu.wpi.first.wpilibj.{Counter, Spark, Talon}
import squants.motion.AngularVelocity
import com.lynbrookrobotics.potassium.frc.Implicits._
import squants.space.Turns
import squants.time.Frequency

case class ShooterFlywheelHardware(leftMotor: Talon,
                                   rightMotor: Talon,
                                   leftHall: Counter,
                                   rightHall: Counter) extends DoubleFlywheelHardware {
  override val leftVelocity: Signal[Frequency] =
    leftHall.frequency

  override val rightVelocity: Signal[Frequency] =
    rightHall.frequency
}

object ShooterFlywheelHardware {
  def apply(config: ShooterFlywheelConfig): ShooterFlywheelHardware = {
    ShooterFlywheelHardware(
      new Talon(config.ports.leftMotor),
      new Talon(config.ports.rightMotor),
      new Counter(config.ports.leftHall),
      new Counter(config.ports.rightHall)
    )
  }
}
