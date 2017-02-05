resolvers += "Funky-Repo" at "http://team846.github.io/repo"
resolvers += "opencv-maven" at "https://github.com/WPIRoboticsProjects/opencv-maven/raw/mvn-repo"

val potassiumVersion = "0.1.0-0f07acf8"

libraryDependencies += "com.lynbrookrobotics" %% "potassium-vision" % potassiumVersion
libraryDependencies += "com.lynbrookrobotics" %% "potassium-remote" % potassiumVersion

scalaVersion in ThisBuild := "2.12.1"
