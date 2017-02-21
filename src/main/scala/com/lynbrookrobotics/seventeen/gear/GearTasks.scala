package com.lynbrookrobotics.seventeen.gear

import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.events.ImpulseEvent
import com.lynbrookrobotics.potassium.frc.ProximitySensor
import com.lynbrookrobotics.potassium.lighting.{DisplayLighting, LightingComponent}
import squants.time.Seconds
import com.lynbrookrobotics.potassium.tasks.{FiniteTask, WaitTask}
import com.lynbrookrobotics.seventeen.driver.DriverHardware
import com.lynbrookrobotics.seventeen.gear.tilter.{ExtendTilter, GearTilter, RetractTilter}
import com.lynbrookrobotics.seventeen.gear.grabber._
import edu.wpi.first.wpilibj.Joystick
import com.lynbrookrobotics.potassium.tasks.WaitForImpulseTask
import com.lynbrookrobotics.potassium.frc.Implicits._

object GearTasks {
  def loadGearFromGroundAbortable(lightingEffect: Int, buttonOverride: Int, buttonTrigger: Int, lightingComponent: LightingComponent)
                                 (implicit tilter: GearTilter,
                                  grabber: GearGrabber,
                                  props: Signal[GearGrabberProperties],
                                  hardware: GearGrabberHardware,
                                  clock: Clock,
                                  driverHardware: DriverHardware, polling: ImpulseEvent): FiniteTask = {
    val liftGear = new WaitTask(Seconds(0.3)).andUntilDone(
      new CloseGrabber()) then new WaitTask(Seconds(0.3)) andUntilDone new RetractTilter()

    val pickUpGear = new OpenGrabberUntilGearAbortable(buttonOverride).andUntilDone(
      new ExtendTilter()
    )

    pickUpGear.then(new WaitForImpulseTask(driverHardware.operatorJoystick.buttonPressed(buttonTrigger).onEnd))
      .andUntilDone(new DisplayLighting(Signal.constant(lightingEffect), lightingComponent)) then liftGear
  }
}
