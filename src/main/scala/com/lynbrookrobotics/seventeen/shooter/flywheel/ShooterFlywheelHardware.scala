package com.lynbrookrobotics.seventeen.shooter.flywheel

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.commons.flywheel.DoubleFlywheelHardware
import com.lynbrookrobotics.potassium.frc.Implicits._
import edu.wpi.first.wpilibj.{Counter, Talon}
import squants.time.Frequency

case class ShooterFlywheelHardware(leftMotor: Talon,
                                   rightMotor: Talon,
                                   leftHall: Counter,
                                   rightHall: Counter) extends DoubleFlywheelHardware {
  override val leftVelocity: Signal[Frequency] =
    leftHall.frequency.map(_ / 2)

  override val rightVelocity: Signal[Frequency] =
    rightHall.frequency.map(_ / 2)
}

object ShooterFlywheelHardware {
  def apply(config: ShooterFlywheelConfig): ShooterFlywheelHardware = {
    ShooterFlywheelHardware(
      {
        val it = new Talon(config.ports.leftMotor)
        it.setInverted(true)
        it
      },
      new Talon(config.ports.rightMotor),
      new Counter(config.ports.leftHall),
      new Counter(config.ports.rightHall)
    )
  }
}
