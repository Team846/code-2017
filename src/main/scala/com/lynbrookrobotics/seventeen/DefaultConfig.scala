package com.lynbrookrobotics.seventeen

object DefaultConfig {
  val defaultConfig = """{
                        |  "driver": {
                        |    "driverPort": 0,
                        |    "operatorPort": 1,
                        |    "driverWheelPort": 2,
                        |    "launchpadPort": -1
                        |  },
                        |  "drivetrain": {
                        |    "ports": {
                        |      "rightBack": 13,
                        |      "rightFront": 12,
                        |      "leftBack": 11,
                        |      "leftFront": 10
                        |    },
                        |    "properties": {
                        |      "maxLeftVelocity": [
                        |        21.9,
                        |        "FeetPerSecond$"
                        |      ],
                        |      "maxRightVelocity": [
                        |        23.1,
                        |        "FeetPerSecond$"
                        |      ],
                        |      "maxAcceleration": [
                        |        0,
                        |        "MetersPerSecondSquared$"
                        |      ],
                        |      "wheelDiameter": [
                        |        4,
                        |        "Inches$"
                        |      ],
                        |      "track": [
                        |        21.75,
                        |        "Inches$"
                        |      ],
                        |      "gearRatio": 0.5,
                        |      "turnControlGains": {
                        |        "kp": {
                        |          "num": [
                        |            50,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            360,
                        |            "DegreesPerSecond$"
                        |          ]
                        |        },
                        |        "ki": {
                        |          "num": [
                        |            0,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            1,
                        |            "Degrees$"
                        |          ]
                        |        },
                        |        "kd": {
                        |          "num": [
                        |            0,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            1,
                        |            "DegreesPerSecond$ / s"
                        |          ]
                        |        }
                        |      },
                        |      "forwardPositionControlGains": {
                        |        "kp": {
                        |          "num": [
                        |            100,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            2,
                        |            "Feet$"
                        |          ]
                        |        },
                        |        "ki": {
                        |          "num": [
                        |            0,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            1,
                        |            "Feet$ * s"
                        |          ]
                        |        },
                        |        "kd": {
                        |          "num": [
                        |            0,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            1,
                        |            "FeetPerSecond$"
                        |          ]
                        |        }
                        |      },
                        |      "turnPositionControlGains": {
                        |        "kp": {
                        |          "num": [
                        |            100,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            90,
                        |            "Degrees$"
                        |          ]
                        |        },
                        |        "ki": {
                        |          "num": [
                        |            0,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            1,
                        |            "Degrees$ * s"
                        |          ]
                        |        },
                        |        "kd": {
                        |          "num": [
                        |            0,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            1,
                        |            "DegreesPerSecond$"
                        |          ]
                        |        }
                        |      },
                        |      "leftControlGains": {
                        |        "kp": {
                        |          "num": [
                        |            30,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            5,
                        |            "FeetPerSecond$"
                        |          ]
                        |        },
                        |        "ki": {
                        |          "num": [
                        |            0,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            1,
                        |            "Meters$"
                        |          ]
                        |        },
                        |        "kd": {
                        |          "num": [
                        |            0,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            1,
                        |            "MetersPerSecondSquared$"
                        |          ]
                        |        }
                        |      },
                        |      "rightControlGains": {
                        |        "kp": {
                        |          "num": [
                        |            30,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            5,
                        |            "FeetPerSecond$"
                        |          ]
                        |        },
                        |        "ki": {
                        |          "num": [
                        |            0,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            1,
                        |            "Meters$"
                        |          ]
                        |        },
                        |        "kd": {
                        |          "num": [
                        |            0,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            1,
                        |            "MetersPerSecondSquared$"
                        |          ]
                        |        }
                        |      },
                        |      "currentLimit": [
                        |        50,
                        |        "Percent$"
                        |      ],
                        |      "defaultLookAheadDistance": [
                        |        1,
                        |        "Feet$"
                        |      ]
                        |    }
                        |  },
                        |  "agitator": null,
                        |  "camSelect": {
                        |    "port": {
                        |      "leftCamPort": 5804,
                        |      "rightCamPort": 5805,
                        |      "driveCamPort": 5811
                        |    },
                        |    "properties": {
                        |      "coprocessorHostname": "10.8.64.20",
                        |      "mjpegPath": "/?action=stream"
                        |    }
                        |  },
                        |  "climberPuller": {
                        |    "ports": {
                        |      "motorChannelA": 14,
                        |      "motorChannelB": 15
                        |    },
                        |    "props": {
                        |      "climbSpeed": [
                        |        100,
                        |        "Percent$"
                        |      ]
                        |    }
                        |  },
                        |  "collectorElevator": null,
                        |  "collectorExtender": null,
                        |  "collectorRollers": null,
                        |  "gearRoller": {
                        |    "ports":{
                        |      "motor": 16
                        |    },
                        |    "props": {
                        |      "defaultHoldingPower": [
                        |        10,
                        |        "Percent$"
                        |      ],
                        |      "intakeGearPower": [
                        |        100,
                        |        "Percent$"
                        |      ],
                        |      "emitGearPower": [
                        |        -50,
                        |        "Percent$"
                        |      ],
                        |      "gearDetectionCurrent": [
                        |        10,
                        |        "Amperes$"
                        |      ]
                        |    }
                        |  },
                        |  "gearTilter": {
                        |    "port": {
                        |      "pneumatic": 0
                        |    }
                        |  },
                        |  "shooterFlywheel": null,
                        |  "shooterShifter": null,
                        |  "loadTray": null
                        |}""".stripMargin
}
