package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.seventeen.agitator.AgitatorHardware
import com.lynbrookrobotics.seventeen.camselect.CamSelectHardware
import com.lynbrookrobotics.seventeen.climber.puller.ClimberPullerHardware
import com.lynbrookrobotics.seventeen.collector.elevator.CollectorElevatorHardware
import com.lynbrookrobotics.seventeen.collector.extender.CollectorExtenderHardware
import com.lynbrookrobotics.seventeen.collector.rollers.CollectorRollersHardware
import com.lynbrookrobotics.seventeen.driver.DriverHardware
import com.lynbrookrobotics.seventeen.drivetrain.DrivetrainHardware
import com.lynbrookrobotics.seventeen.gear.grabber.GearGrabberHardware
import com.lynbrookrobotics.seventeen.gear.tilter.GearTilterHardware
import com.lynbrookrobotics.seventeen.shooter.flywheel.ShooterFlywheelHardware
import com.lynbrookrobotics.seventeen.shooter.shifter.ShooterShifterHardware
import edu.wpi.first.wpilibj.PowerDistributionPanel

case class RobotHardware(driver: DriverHardware,
                         drivetrain: DrivetrainHardware,
                         agitator: AgitatorHardware,
                         camSelect: CamSelectHardware,
                         climberPuller: ClimberPullerHardware,
                         collectorElevator: CollectorElevatorHardware,
                         collectorExtender: CollectorExtenderHardware,
                         collectorRollers: CollectorRollersHardware,
                         gearGrabber: GearGrabberHardware,
                         gearTilter: GearTilterHardware,
                         shooterFlywheel: ShooterFlywheelHardware,
                         shooterShifter: ShooterShifterHardware,
                         pdp: PowerDistributionPanel)

object RobotHardware {
  def apply(robotConfig: RobotConfig): RobotHardware = {
    val driver = DriverHardware(robotConfig.driver)

    import robotConfig._

    RobotHardware(
      driver = driver,
      drivetrain = if (drivetrain != null) DrivetrainHardware(drivetrain, driver) else null,
      agitator = if (agitator != null) AgitatorHardware(agitator) else null,
      camSelect = if (camSelect != null) CamSelectHardware(camSelect) else null,
      climberPuller = if (climberPuller != null) ClimberPullerHardware(climberPuller) else null,
      collectorElevator = if (collectorElevator != null) CollectorElevatorHardware(collectorElevator) else null,
      collectorExtender = if (collectorExtender != null) CollectorExtenderHardware(collectorExtender) else null,
      collectorRollers = if (collectorRollers != null) CollectorRollersHardware(collectorRollers) else null,
      gearGrabber = if (gearGrabber != null) GearGrabberHardware(gearGrabber) else null,
      gearTilter = if (gearTilter != null) GearTilterHardware(gearTilter) else null,
      shooterFlywheel = if (shooterFlywheel != null) ShooterFlywheelHardware(shooterFlywheel) else null,
      shooterShifter = if (shooterShifter != null) ShooterShifterHardware(shooterShifter) else null,
      new PowerDistributionPanel()
    )
  }
}
