enablePlugins(FRCPlugin)

organization := "com.lynbrookrobotics"
teamNumber := 846

scalaVersion := "2.12.1"

name := "code-2017"

version := "0.1.0-SNAPSHOT"

coverageEnabled := true

resolvers += "Funky-Repo" at "http://team846.github.io/repo"
resolvers += "WPILib-Maven" at "http://team846.github.io/wpilib-maven"
resolvers += "opencv-maven" at "https://github.com/WPIRoboticsProjects/opencv-maven/raw/mvn-repo"

libraryDependencies += "com.lynbrookrobotics" %% "potassium-core" % "0.1.0-SNAPSHOT"
libraryDependencies += "com.lynbrookrobotics" %% "potassium-commons" % "0.1.0-SNAPSHOT"
libraryDependencies += "com.lynbrookrobotics" %% "potassium-frc" % "0.1.0-SNAPSHOT"

libraryDependencies += "com.lynbrookrobotics" %% "funky-dashboard" % "0.2.0-SNAPSHOT"

libraryDependencies += "edu.wpi.first" % "wpilib" % "2017.2.1"
libraryDependencies += "edu.wpi.first" % "networktables" % "2017.2.1"
libraryDependencies += "com.ctre" % "ctrlib" % "4.4.1.9"
libraryDependencies += "org.opencv" % "opencv-java" % "3.1.0"
