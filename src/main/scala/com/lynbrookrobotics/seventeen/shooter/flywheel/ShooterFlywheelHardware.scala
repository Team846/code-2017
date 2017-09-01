package com.lynbrookrobotics.seventeen.shooter.flywheel

import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.commons.flywheel.DoubleFlywheelHardware
import com.lynbrookrobotics.potassium.frc.Implicits._
import edu.wpi.first.wpilibj.{Counter, Talon}
import squants.time.Frequency

case class ShooterFlywheelHardware(leftMotor: Talon,
                                   rightMotor: Talon,
                                   leftHall: Counter,
                                   rightHall: Counter) extends DoubleFlywheelHardware {
  override lazy val leftVelocity: Stream[Frequency] = ???
    //leftHall.frequency.map(_ / 2) // 2 magnets on the roller

  override lazy val rightVelocity: Stream[Frequency] = ???
//    rightHall.frequency.map(_ / 2)  // 2 magnets on the roller

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
