enablePlugins(FRCPlugin)

organization := "com.lynbrookrobotics"
teamNumber := 846

scalaVersion := "2.12.3"

name := "code-2017"

version := "0.1.0-SNAPSHOT"

//offline := true
lazy val commons = project
lazy val vision = project.dependsOn(commons).enablePlugins(AssemblyPlugin)

resolvers += "Funky-Repo" at "http://team846.github.io/repo"
resolvers += "WPILib-Maven" at "http://team846.github.io/wpilib-maven"
resolvers += "opencv-maven" at "https://github.com/WPIRoboticsProjects/opencv-maven/raw/mvn-repo"

libraryDependencies += "com.lynbrookrobotics" %% "potassium-core" % Versions.potassiumVersion
libraryDependencies += "com.lynbrookrobotics" %% "potassium-control" % Versions.potassiumVersion
libraryDependencies += "com.lynbrookrobotics" %% "potassium-commons" % Versions.potassiumVersion
libraryDependencies += "com.lynbrookrobotics" %% "potassium-frc" % Versions.potassiumVersion
libraryDependencies += "com.lynbrookrobotics" %% "potassium-config" % Versions.potassiumVersion
libraryDependencies += "com.lynbrookrobotics" %% "potassium-lighting" % Versions.potassiumVersion

libraryDependencies += "org.mockito" % "mockito-core" % "2.3.11" % Test

libraryDependencies += "com.lynbrookrobotics" %% "funky-dashboard" % "0.3.0-SNAPSHOT"

libraryDependencies += "edu.wpi.first" % "wpilib" % Versions.wpiVersion
libraryDependencies += "edu.wpi.first" % "cscore" % Versions.wpiVersion
libraryDependencies += "edu.wpi.first" % "ntcore" % Versions.wpiVersion
libraryDependencies += "edu.wpi.first" % "wpiutil" % Versions.wpiVersion
libraryDependencies += "com.ctre" % "phoenix" % "5.1.3.1"
libraryDependencies += "org.opencv" % "opencv-java" % "3.1.0"

libraryDependencies += "com.google.guava" % "guava" % "21.0"
