enablePlugins(FRCPlugin, TravisScalaStylePlugin)

organization := "com.lynbrookrobotics"
teamNumber := 846

scalaVersion := "2.12.1"

name := "code-2017"

version := "0.1.0-SNAPSHOT"

resolvers += "Funky-Repo" at "http://team846.github.io/repo"
resolvers += "WPILib-Maven" at "http://team846.github.io/wpilib-maven"
resolvers += "opencv-maven" at "https://github.com/WPIRoboticsProjects/opencv-maven/raw/mvn-repo"

libraryDependencies += "com.lynbrookrobotics" %% "potassium-core" % Versions.potassiumVersion
libraryDependencies += "com.lynbrookrobotics" %% "potassium-control" % Versions.potassiumVersion
libraryDependencies += "com.lynbrookrobotics" %% "potassium-commons" % Versions.potassiumVersion
libraryDependencies += "com.lynbrookrobotics" %% "potassium-frc" % Versions.potassiumVersion
libraryDependencies += "com.lynbrookrobotics" %% "potassium-config" % Versions.potassiumVersion

libraryDependencies += "org.mockito" % "mockito-core" % "2.3.11" % Test

libraryDependencies += "com.lynbrookrobotics" %% "funky-dashboard" % "0.2.0-SNAPSHOT"

libraryDependencies += "edu.wpi.first" % "wpilib" % "2017.2.1"
libraryDependencies += "edu.wpi.first" % "networktables" % "2017.2.1"
libraryDependencies += "com.ctre" % "ctrlib" % "4.4.1.9"
libraryDependencies += "org.opencv" % "opencv-java" % "3.1.0"

lazy val vision = project.enablePlugins(AssemblyPlugin)
