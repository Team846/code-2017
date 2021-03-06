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
                        |      "rightBack": 11,
                        |      "rightFront": 12,
                        |      "leftBack": 14 ,
                        |      "leftFront": 13
                        |    },
                        |    "properties": {
                        |      "maxLeftVelocity": [
                        |        15,
                        |        "FeetPerSecond$"
                        |      ],
                        |      "maxRightVelocity": [
                        |        15,
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
                        |      "gearRatio": 0.3125,
                        |      "turnControlGains": {
                        |        "kp": {
                        |          "num": [
                        |            0,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            1,
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
                        |            10,
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
                        |            10,
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
                        |  "agitator": {
                        |    "ports": {
                        |      "motor": 4
                        |    },
                        |    "properties": {
                        |      "spinSpeed": [
                        |        -100,
                        |        "Percent$"
                        |      ]
                        |    }
                        |  },
                        |  "camSelect": {
                        |    "port": {
                        |      "leftCamPort": 5804,
                        |      "rightCamPort": 5805,
                        |      "driveCamPort": 5803
                        |    },
                        |    "properties": {
                        |      "coprocessorHostname": "10.8.46.19",
                        |      "mjpegPath": "/stream.mjpg"
                        |    }
                        |  },
                        |  "climberPuller": {
                        |    "ports": {
                        |      "motorChannelA": 15,
                        |      "motorChannelB": 16
                        |    },
                        |    "props": {
                        |      "climbSpeed": [
                        |        100,
                        |        "Percent$"
                        |      ]
                        |    }
                        |  },
                        |  "collectorElevator": {
                        |    "port": {
                        |      "motor": 1
                        |    },
                        |    "properties": {
                        |      "collectSpeed": [
                        |        100,
                        |        "Percent$"
                        |      ]
                        |    }
                        |  },
                        |  "collectorExtender": {
                        |    "port": {
                        |      "pneumatic": 3
                        |    }
                        |  },
                        |  "collectorRollers": {
                        |    "ports": {
                        |      "rollerChannel": 0
                        |    },
                        |    "properties": {
                        |      "lowRollerSpeedOutput": [
                        |        0,
                        |        "Percent$"
                        |      ],
                        |      "highRollerSpeedOutput": [
                        |        100,
                        |        "Percent$"
                        |      ]
                        |    }
                        |  },
                        |  "gearGrabber": {
                        |    "port": {
                        |      "pneumatic": 1,
                        |      "proximitySensor": 0
                        |    },
                        |    "props": {
                        |      "detectingDistance": [
                        |        2.2,
                        |        "Volts$"
                        |      ]
                        |    }
                        |  },
                        |  "gearTilter": {
                        |    "port": {
                        |      "pneumatic": 2
                        |    }
                        |  },
                        |  "shooterFlywheel": {
                        |    "ports": {
                        |      "leftMotor": 2,
                        |      "rightMotor": 3,
                        |      "leftHall": 0,
                        |      "rightHall": 1
                        |    },
                        |    "props": {
                        |      "maxVelocityLeft": [
                        |        6500,
                        |        "RevolutionsPerMinute$"
                        |      ],
                        |      "maxVelocityRight": [
                        |        6500,
                        |        "RevolutionsPerMinute$"
                        |      ],
                        |      "velocityGainsLeft": {
                        |        "kp": {
                        |          "num": [
                        |            100,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            3000,
                        |            "RevolutionsPerMinute$"
                        |          ]
                        |        },
                        |        "ki": {
                        |          "num": [
                        |            0,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            1000,
                        |            "Each$"
                        |          ]
                        |        },
                        |        "kd": {
                        |          "num": [
                        |            0,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            1000,
                        |            "RevolutionsPerMinute$ / s"
                        |          ]
                        |        }
                        |      },
                        |      "velocityGainsRight": {
                        |        "kp": {
                        |          "num": [
                        |            100,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            3000,
                        |            "RevolutionsPerMinute$"
                        |          ]
                        |        },
                        |        "ki": {
                        |          "num": [
                        |            0,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            1000,
                        |            "Each$"
                        |          ]
                        |        },
                        |        "kd": {
                        |          "num": [
                        |            0,
                        |            "Percent$"
                        |          ],
                        |          "den": [
                        |            1000,
                        |            "RevolutionsPerMinute$ / s"
                        |          ]
                        |        }
                        |      },
                        |      "lowShootSpeedLeft": [
                        |        2000,
                        |        "RevolutionsPerMinute$"
                        |      ],
                        |      "lowShootSpeedRight": [
                        |        2000,
                        |        "RevolutionsPerMinute$"
                        |      ],
                        |      "midShootSpeedLeft": [
                        |        4300,
                        |        "RevolutionsPerMinute$"
                        |      ],
                        |      "midShootSpeedRight": [
                        |        4300,
                        |        "RevolutionsPerMinute$"
                        |      ],
                        |      "fastShootSpeedLeft": [
                        |        6000,
                        |        "RevolutionsPerMinute$"
                        |      ],
                        |      "fastShootSpeedRight": [
                        |        6000,
                        |        "RevolutionsPerMinute$"
                        |      ],
                        |      "currentLimit": [
                        |        40,
                        |        "Percent$"
                        |      ],
                        |      "speedTolerance": [
                        |        75,
                        |        "RevolutionsPerMinute$"
                        |      ]
                        |    }
                        |  },
                        |  "shooterShifter": {
                        |    "ports": {
                        |      "pneumatic": 0
                        |    }
                        |  },
                        |  "loadTray": {
                        |    "port": {
                        |      "pneumatic": 4
                        |    }
                        |  }
                        |}
                        |""".stripMargin
}
