package com.lynbrookrobotics.seventeen

import com.lynbrookrobotics.potassium.frc.WPIClock
import edu.wpi.first.wpilibj.{RobotBase, Threads}
import squants.time.Milliseconds

import scala.collection.mutable
import scala.reflect.io.File

//import java.io.File
//
//import com.google.common.reflect.ClassPath
//import com.lynbrookrobotics.potassium.Signal
//import com.lynbrookrobotics.potassium.config.TwoWayFile
//import com.lynbrookrobotics.potassium.events.ImpulseEventSource
//import com.lynbrookrobotics.potassium.frc.Implicits._
//import upickle.default._
//import com.lynbrookrobotics.potassium.config.SquantsPickling._
//import sun.plugin.util.NotifierObject

class LaunchRobot extends RobotBase {
  //  val targetFile = new File("/home/lvuser/robot-config.json")
  //  if (!targetFile.exists()) {
  //    targetFile.createNewFile()
  //  }
  //
  //  protected val configFile = new TwoWayFile(targetFile)
  //  protected val parsedConfig = configFile.map { string =>
  //    val ret: RobotConfig = try {
  //      read[RobotConfig](string)
  //    } catch {
  //      case _ =>
  //        println("BAD BAD DEFAULTING CONFIG")
  //        read[RobotConfig](
  //          """
  //            |{
  //            |  "driver": {
  //            |    "driverPort": 0,
  //            |    "operatorPort": 1,
  //            |    "driverWheelPort": 2,
  //            |    "launchpadPort": -1
  //            |  },
  //            |  "drivetrain": {
  //            |    "ports": {
  //            |      "rightBack": 11,
  //            |      "rightFront": 12,
  //            |      "leftBack": 14 ,
  //            |      "leftFront": 13
  //            |    },
  //            |    "properties": {
  //            |      "maxLeftVelocity": [
  //            |        15,
  //            |        "FeetPerSecond$"
  //            |      ],
  //            |      "maxRightVelocity": [
  //            |        15,
  //            |        "FeetPerSecond$"
  //            |      ],
  //            |      "maxAcceleration": [
  //            |        0,
  //            |        "MetersPerSecondSquared$"
  //            |      ],
  //            |      "wheelDiameter": [
  //            |        4,
  //            |        "Inches$"
  //            |      ],
  //            |      "track": [
  //            |        21.75,
  //            |        "Inches$"
  //            |      ],
  //            |      "gearRatio": 0.3125,
  //            |      "turnControlGains": {
  //            |        "kp": {
  //            |          "num": [
  //            |            0,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            1,
  //            |            "DegreesPerSecond$"
  //            |          ]
  //            |        },
  //            |        "ki": {
  //            |          "num": [
  //            |            0,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            1,
  //            |            "Degrees$"
  //            |          ]
  //            |        },
  //            |        "kd": {
  //            |          "num": [
  //            |            0,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            1,
  //            |            "DegreesPerSecond$ / s"
  //            |          ]
  //            |        }
  //            |      },
  //            |      "forwardPositionControlGains": {
  //            |        "kp": {
  //            |          "num": [
  //            |            100,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            2,
  //            |            "Feet$"
  //            |          ]
  //            |        },
  //            |        "ki": {
  //            |          "num": [
  //            |            0,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            1,
  //            |            "Feet$ * s"
  //            |          ]
  //            |        },
  //            |        "kd": {
  //            |          "num": [
  //            |            0,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            1,
  //            |            "FeetPerSecond$"
  //            |          ]
  //            |        }
  //            |      },
  //            |      "turnPositionControlGains": {
  //            |        "kp": {
  //            |          "num": [
  //            |            75,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            90,
  //            |            "Degrees$"
  //            |          ]
  //            |        },
  //            |        "ki": {
  //            |          "num": [
  //            |            0,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            1,
  //            |            "Degrees$ * s"
  //            |          ]
  //            |        },
  //            |        "kd": {
  //            |          "num": [
  //            |            0,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            1,
  //            |            "DegreesPerSecond$"
  //            |          ]
  //            |        }
  //            |      },
  //            |      "leftControlGains": {
  //            |        "kp": {
  //            |          "num": [
  //            |            10,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            5,
  //            |            "FeetPerSecond$"
  //            |          ]
  //            |        },
  //            |        "ki": {
  //            |          "num": [
  //            |            0,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            1,
  //            |            "Meters$"
  //            |          ]
  //            |        },
  //            |        "kd": {
  //            |          "num": [
  //            |            0,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            1,
  //            |            "MetersPerSecondSquared$"
  //            |          ]
  //            |        }
  //            |      },
  //            |      "rightControlGains": {
  //            |        "kp": {
  //            |          "num": [
  //            |            10,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            5,
  //            |            "FeetPerSecond$"
  //            |          ]
  //            |        },
  //            |        "ki": {
  //            |          "num": [
  //            |            0,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            1,
  //            |            "Meters$"
  //            |          ]
  //            |        },
  //            |        "kd": {
  //            |          "num": [
  //            |            0,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            1,
  //            |            "MetersPerSecondSquared$"
  //            |          ]
  //            |        }
  //            |      },
  //            |      "currentLimit": [
  //            |        75,
  //            |        "Percent$"
  //            |      ],
  //            |      "defaultLookAheadDistance": [
  //            |        1,
  //            |        "Feet$"
  //            |      ]
  //            |    }
  //            |  },
  //            |  "agitator": {
  //            |    "ports": {
  //            |      "motor": 4
  //            |    },
  //            |    "properties": {
  //            |      "spinSpeed": [
  //            |        -100,
  //            |        "Percent$"
  //            |      ]
  //            |    }
  //            |  },
  //            |  "camSelect": {
  //            |    "port": {
  //            |      "leftCamPort": 5804,
  //            |      "rightCamPort": 5805,
  //            |      "driveCamPort": 5803
  //            |    },
  //            |    "properties": {
  //            |      "coprocessorHostname": "10.8.46.19",
  //            |      "mjpegPath": "/stream.mjpg"
  //            |    }
  //            |  },
  //            |  "climberPuller": {
  //            |    "ports": {
  //            |      "motorChannelA": 15,
  //            |      "motorChannelB": 16
  //            |    },
  //            |    "props": {
  //            |      "climbSpeed": [
  //            |        100,
  //            |        "Percent$"
  //            |      ]
  //            |    }
  //            |  },
  //            |  "collectorElevator": {
  //            |    "port": {
  //            |      "motor": 1
  //            |    },
  //            |    "properties": {
  //            |      "collectSpeed": [
  //            |        100,
  //            |        "Percent$"
  //            |      ]
  //            |    }
  //            |  },
  //            |  "collectorExtender": {
  //            |    "port": {
  //            |      "pneumatic": 3
  //            |    }
  //            |  },
  //            |  "collectorRollers": {
  //            |    "ports": {
  //            |      "rollerChannel": 0
  //            |    },
  //            |    "properties": {
  //            |      "lowRollerSpeedOutput": [
  //            |        0,
  //            |        "Percent$"
  //            |      ],
  //            |      "highRollerSpeedOutput": [
  //            |        100,
  //            |        "Percent$"
  //            |      ]
  //            |    }
  //            |  },
  //            |  "gearGrabber": {
  //            |    "port": {
  //            |      "pneumatic": 1,
  //            |      "proximitySensor": 0
  //            |    },
  //            |    "props": {
  //            |      "detectingDistance": [
  //            |        2.2,
  //            |        "Volts$"
  //            |      ]
  //            |    }
  //            |  },
  //            |  "gearTilter": {
  //            |    "port": {
  //            |      "pneumatic": 2
  //            |    }
  //            |  },
  //            |  "shooterFlywheel": {
  //            |    "ports": {
  //            |      "leftMotor": 2,
  //            |      "rightMotor": 3,
  //            |      "leftHall": 0,
  //            |      "rightHall": 1
  //            |    },
  //            |    "props": {
  //            |      "maxVelocityLeft": [
  //            |        6250,
  //            |        "RevolutionsPerMinute$"
  //            |      ],
  //            |      "maxVelocityRight": [
  //            |        6250,
  //            |        "RevolutionsPerMinute$"
  //            |      ],
  //            |      "velocityGainsLeft": {
  //            |        "kp": {
  //            |          "num": [
  //            |            60,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            1000,
  //            |            "RevolutionsPerMinute$"
  //            |          ]
  //            |        },
  //            |        "ki": {
  //            |          "num": [
  //            |            0,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            1000,
  //            |            "Each$"
  //            |          ]
  //            |        },
  //            |        "kd": {
  //            |          "num": [
  //            |            0,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            1000,
  //            |            "RevolutionsPerMinute$ / s"
  //            |          ]
  //            |        }
  //            |      },
  //            |      "velocityGainsRight": {
  //            |        "kp": {
  //            |          "num": [
  //            |            60,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            1000,
  //            |            "RevolutionsPerMinute$"
  //            |          ]
  //            |        },
  //            |        "ki": {
  //            |          "num": [
  //            |            0,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            1000,
  //            |            "Each$"
  //            |          ]
  //            |        },
  //            |        "kd": {
  //            |          "num": [
  //            |            0,
  //            |            "Percent$"
  //            |          ],
  //            |          "den": [
  //            |            1000,
  //            |            "RevolutionsPerMinute$ / s"
  //            |          ]
  //            |        }
  //            |      },
  //            |      "lowShootSpeedLeft": [
  //            |        2000,
  //            |        "RevolutionsPerMinute$"
  //            |      ],
  //            |      "lowShootSpeedRight": [
  //            |        2000,
  //            |        "RevolutionsPerMinute$"
  //            |      ],
  //            |      "midShootSpeedLeft": [
  //            |        3800,
  //            |        "RevolutionsPerMinute$"
  //            |      ],
  //            |      "midShootSpeedRight": [
  //            |        3800,
  //            |        "RevolutionsPerMinute$"
  //            |      ],
  //            |      "fastShootSpeedLeft": [
  //            |        6000,
  //            |        "RevolutionsPerMinute$"
  //            |      ],
  //            |      "fastShootSpeedRight": [
  //            |        6000,
  //            |        "RevolutionsPerMinute$"
  //            |      ],
  //            |      "currentLimit": [
  //            |        0,
  //            |        "Percent$"
  //            |      ],
  //            |      "speedTolerance": [
  //            |        50,
  //            |        "RevolutionsPerMinute$"
  //            |      ]
  //            |    }
  //            |  },
  //            |  "shooterShifter": {
  //            |    "ports": {
  //            |      "pneumatic": 0
  //            |    }
  //            |  }
  //            |}
  //          """.stripMargin)
  //    }
  //
  //    ret
  //  }(
  //    (_, newValue) => write(newValue)
  //  )
  //
  //  private implicit val config = Signal {
  //    parsedConfig.value
  //  }
  //
  //  private implicit val hardware = RobotHardware(config.get)
  //
  //  private var coreRobot: CoreRobot = null
  //
  //  private val ds = m_ds
  //
  //  private val eventPollingSource = new ImpulseEventSource
  //  private implicit val eventPolling = eventPollingSource.event

  //  override def startCompetition(): Unit = {
  //    coreRobot = new CoreRobot(
  //      Signal(configFile.value),
  //      newS => {
  ////        println(newS.toString.substring(0, 200))
  //        val oldS = configFile.value
  //        try {
  //          configFile.value = newS
  //        } catch {
  //          case _ => configFile.value = oldS
  //        }
  //      }
  //    )
  //
  //    ClassPath.from(Thread.currentThread().getContextClassLoader).
  //      getTopLevelClassesRecursive("com.lynbrookrobotics").
  //      forEach(c => println(s"preloaded ${c.getName}"))
  //
  //    ClassPath.from(Thread.currentThread().getContextClassLoader).
  //      getTopLevelClassesRecursive("squants").
  //      forEach(c => println(s"preloaded ${c.getName}"))
  //
  //    ClassPath.from(Thread.currentThread().getContextClassLoader).
  //      getTopLevelClassesRecursive("edu.wpi.first.wpilibj").
  //      forEach(c => println(s"preloaded ${c.getName}"))
  //
  //    ClassPath.from(Thread.currentThread().getContextClassLoader).
  //      getTopLevelClassesRecursive("com.ctre").
  //      forEach(c => println(s"preloaded ${c.getName}"))
  //
  //    coreRobot.comms.foreach(_.connect())
  //
  //    WakeOnLan.awaken("B8:AE:ED:7E:78:E1")
  //
  //    HAL.observeUserProgramStarting()
  //
  //    while (true) {
  //      ds.waitForData()
  //      eventPollingSource.fire()
  //    }
  //  }

  def startCompetition(): Unit = {
    import edu.wpi.first.wpilibj.{Notifier, Utility}

    val maxDataSize = 250
    val data = new mutable.ArrayBuffer[Long](maxDataSize)
    val writer = File(s"/tmp/raw_notfier_jitter_every_${maxDataSize}_ticks_${System.currentTimeMillis()}").printWriter()
    var startTime = 0L

    var dataSize = 0

    var first = true
//    val notfier = new Notifier(() => {
//      if (first) {
//        Thread.currentThread().setName("Some-Notifier-Thread")
//
////        Thread.currentThread().setPriority(1)
//        Threads.setCurrentThreadPriority(true, 1)
//        first = false
//      }
//
//      data.+=(Utility.getFPGATime)
//      dataSize += 1
//      if (dataSize == maxDataSize) {
//        writer.println("new thread id: " + Thread.currentThread().getId + s"name ${Thread.currentThread().getName} priority ${Thread.currentThread().getPriority}")
//        data.foreach(l => writer.println(l))
//        writer.flush()
//        writer.close()
//      }
//    })

    val clock = WPIClock(Milliseconds(5)) { dt =>
//      if (first) {
//          Thread.currentThread().setName("Some-Notifier-Thread")
//
//  //        Thread.currentThread().setPriority(1)
//          Threads.setCurrentThreadPriority(true, 1)
//          first = false
//        }

        data.+=(Utility.getFPGATime)
        dataSize += 1
        if (dataSize == maxDataSize) {
          writer.println("constatn thunk period thread id: " + Thread.currentThread().getId + s"name ${Thread.currentThread().getName} priority ${Thread.currentThread().getPriority}")
          data.foreach(l => writer.println(l))
          writer.flush()
          writer.close()
        }
    }
    data.append(Utility.getFPGATime)
    data.append(-1)

    data.append(Utility.getFPGATime)
    data.append(-1)
    while (true) {}
  }
}
