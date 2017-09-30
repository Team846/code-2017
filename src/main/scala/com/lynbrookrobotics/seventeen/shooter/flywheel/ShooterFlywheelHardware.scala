package com.lynbrookrobotics.seventeen.shooter.flywheel

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.flywheel.DoubleFlywheelHardware
import com.lynbrookrobotics.potassium.frc.Implicits._
import com.lynbrookrobotics.potassium.streams.Stream
import edu.wpi.first.wpilibj.{Counter, Talon}
import squants.time.{Frequency, Milliseconds}

case class ShooterFlywheelHardware(leftMotor: Talon,
                                   rightMotor: Talon,
                                   leftHall: Counter,
                                   rightHall: Counter)(implicit clock: Clock) extends DoubleFlywheelHardware {
  val velocities: Stream[(Frequency, Frequency)] = Stream.periodic(Milliseconds(5)) {
    (leftHall.frequency / 2, rightHall.frequency / 2)
  }
  override lazy val leftVelocity: Stream[Frequency] = velocities.map(_._1)

  override lazy val rightVelocity: Stream[Frequency] = velocities.map(_._2)

}

object ShooterFlywheelHardware {
  def apply(config: ShooterFlywheelConfig)(implicit clock: Clock): ShooterFlywheelHardware = {
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
