enablePlugins(FRCPlugin)

organization := "com.lynbrookrobotics"
teamNumber := 846

scalaVersion := "2.11.8"

name := "workshops-2016"

version := "0.1.0-SNAPSHOT"

resolvers += "Funky-Repo" at "http://team846.github.io/repo"
resolvers += "WPILib-Maven" at "http://team846.github.io/wpilib-maven"

libraryDependencies += "com.lynbrookrobotics" %% "potassium-core" % "0.1.0-SNAPSHOT"
libraryDependencies += "com.lynbrookrobotics" %% "potassium-frc" % "0.1.0-SNAPSHOT"
libraryDependencies += "com.lynbrookrobotics" %% "funky-dashboard" % "0.2.0-SNAPSHOT"

libraryDependencies += "edu.wpi.first" % "wpilib" % "0.1.0.201603020231"
libraryDependencies += "edu.wpi.first" % "networktables" % "0.1.0.201603020231"

staticIP := true