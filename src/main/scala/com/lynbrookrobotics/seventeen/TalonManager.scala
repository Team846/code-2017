package com.lynbrookrobotics.seventeen

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.{NeutralMode, StatusFrameEnhanced}

// todo move to potassium
object TalonManager {
  def resetTalonToDefaults(it: TalonSRX): Unit = {
    val escTout = 0
    it.setNeutralMode(NeutralMode.Coast)
    it.configOpenloopRamp(0, escTout)
    it.configClosedloopRamp(0, escTout)

    it.configPeakOutputReverse(-1, escTout)
    it.configNominalOutputReverse(0, escTout)
    it.configNominalOutputForward(0, escTout)
    it.configPeakOutputForward(1, escTout)
    it.configNeutralDeadband(0.001 /*min*/ , escTout)

    it.configVoltageCompSaturation(11, escTout)
    it.configVoltageMeasurementFilter(32, escTout)
    it.enableVoltageCompensation(true)

    it.configContinuousCurrentLimit(75, escTout)
    it.configPeakCurrentDuration(0, escTout)
    it.enableCurrentLimit(true)

    import StatusFrameEnhanced._
    Map(
      Status_1_General -> 10,
      Status_2_Feedback0 -> 20,
      Status_12_Feedback1 -> 20,
      Status_3_Quadrature -> 100,
      Status_4_AinTempVbat -> 100
    ).foreach { case (frame, period) =>
      it.setStatusFramePeriod(frame, period, escTout)
    }
  }
}
