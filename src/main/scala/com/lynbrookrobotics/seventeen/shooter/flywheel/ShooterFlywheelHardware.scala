package com.lynbrookrobotics.seventeen.shooter.flywheel

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.commons.flywheel.DoubleFlywheelHardware
import edu.wpi.first.wpilibj.{Counter, Spark}
import squants.motion.AngularVelocity
import com.lynbrookrobotics.potassium.frc.Implicits._
import squants.space.Turns

case class ShooterFlywheelHardware(leftMotor: Spark,
                                   rightMotor: Spark,
                                   leftHall: Counter,
                                   rightHall: Counter) extends DoubleFlywheelHardware {
  override val leftVelocity: Signal[AngularVelocity] =
    leftHall.period.map(p => Turns(1) / p)

  override val rightVelocity: Signal[AngularVelocity] =
    rightHall.period.map(p => Turns(1) / p)
}

object ShooterFlywheelHardware {
  def apply(config: ShooterFlywheelConfig): ShooterFlywheelHardware = {
    ShooterFlywheelHardware(
      new Spark(config.ports.leftMotor),
      new Spark(config.ports.rightMotor),
      new Counter(config.ports.leftHall),
      new Counter(config.ports.rightHall)
    )
  }
}
