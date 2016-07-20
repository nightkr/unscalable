name := "Unscalable"
version := "0.0.1-SNAPSHOT"
initialCommands in console += "import unscalable.Prelude._"
cleanupCommands in console += "actorSystem.terminate()"

scalaVersion := "2.11.8"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.4.8"
