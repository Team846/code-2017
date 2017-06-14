package com.lynbrookrobotics.seventeen.shooter.flywheel

import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.drivetrain.MathUtilities
import com.lynbrookrobotics.potassium.{Component, Signal}
import com.lynbrookrobotics.seventeen.driver.DriverHardware
import edu.wpi.first.wpilibj.Timer
import squants.time.{Frequency, Milliseconds}
import squants.{Dimensionless, Each, Percent}

import scala.collection.mutable
import scala.reflect.io.File


class ShooterFlywheel(implicit properties: Signal[ShooterFlywheelProperties], hardware: ShooterFlywheelHardware, clock: Clock, driverHardware: DriverHardware)
  extends Component[DoubleFlywheelSignal](Milliseconds(5)) {

  val NominalVoltage = 11.9

  override def defaultController = Signal.constant(
    DoubleFlywheelSignal(Percent(0), Percent(0))
  ).toPeriodic

  /**
    * Compensate for reduced battery voltage
    *
    * @return factor to multiply inputs to imitate behaviour of system where battery voltage
    *         is nominal
    */
  def voltageFactor: Double = {
    val batteryVoltage = driverHardware.station.getBatteryVoltage
    if (batteryVoltage > NominalVoltage / 2) { // reasonable measurement must be than 6 volts
      NominalVoltage / batteryVoltage
    } else {
      // Do not apply correction if voltage is unusually low
      1.0
    }
  }

  val leftLogger = new ShotLogger("Left")
  val rightLogger = new ShotLogger("Right")
  val allLogger = new mutable.Queue[(Double, Frequency, Frequency)]()
  val fileInst = File(s"/tmp/${System.currentTimeMillis()}-shotLog.tsv")

  override def applySignal(signal: DoubleFlywheelSignal): Unit = {
    val leftVelocityPercent = Each(hardware.leftVelocity.get / properties.get.maxVelocityLeft)
    val rightVelocityPercent = Each(hardware.rightVelocity.get / properties.get.maxVelocityRight)

    val leftOut = MathUtilities.limitCurrentOutput(signal.left, leftVelocityPercent, properties.get.currentLimit, properties.get.currentLimit)
    val rightOut = MathUtilities.limitCurrentOutput(signal.right, rightVelocityPercent, properties.get.currentLimit, properties.get.currentLimit)

    hardware.leftMotor.set(voltageFactor * leftOut.toEach)
    hardware.rightMotor.set(voltageFactor * rightOut.toEach)

    allLogger.enqueue((Timer.getFPGATimestamp, hardware.leftVelocity.get, hardware.rightVelocity.get))
    if (allLogger.size > 25000) {
      println("WRITING LOGS - SYSTEM WILL FREEZE")
      val out = fileInst.bufferedWriter(true)
      allLogger.foreach(i =>
        out.append(s"\n${i._1}\t${i._2}\t${i._3}")
      )
      out.flush()
      out.close()
      allLogger.clear()
    }
    if (signal.left > Percent(50) && signal.right > Percent(50)) {
      leftLogger.log(signal.left - leftVelocityPercent)
      rightLogger.log(signal.right - rightVelocityPercent)
    }
  }
}
