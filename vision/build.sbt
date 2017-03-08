resolvers += "Funky-Repo" at "http://team846.github.io/repo"
resolvers += "opencv-maven" at "https://github.com/WPIRoboticsProjects/opencv-maven/raw/mvn-repo"

libraryDependencies += "com.lynbrookrobotics" %% "potassium-vision" % Versions.potassiumVersion
libraryDependencies += "com.lynbrookrobotics" %% "potassium-remote" % Versions.potassiumVersion

libraryDependencies += "com.lynbrookrobotics" %% "funky-dashboard" % "0.2.1-SNAPSHOT"

libraryDependencies += "org.opencv" % "opencv-natives-linux-x86_64" % "3.1.0"

scalaVersion in ThisBuild := "2.12.1"
